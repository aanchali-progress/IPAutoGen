package oajava.excel;

import oajava.sql.*; // jdam, schemaobj_table, schemaobj_column, xo_int, xo_long, xo_tm, ip
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.DateUtil;

import static oajava.sql.ip.*; // UL_TM_*, XO_*, SQL_*, DAM_*

/**
 * OpenAccess IP: exposes an Excel workbook as SQL tables (one sheet per table).
 * - Full CRUD + CREATE/DROP TABLE.
 * - Schema persisted alongside the workbook as <xlsx>.schema.
 * - All connection-scoped state lives in ExcelConnection; initialized in ipConnect().
 */
public class damip implements oajava.sql.ip {

    private long m_tmHandle = 0;
    private ExcelConnection conn = null;
    private boolean inTransaction = false;

    private static final String OA_CATALOG_NAME = "SCHEMA";
    private static final String OA_SCHEMA_NAME  = "OAUSER";

    // Capability flags
    private final int[] ip_support_array = {
        0,  // reserved
        1,  // SELECT
        1,  // INSERT
        1,  // UPDATE
        1,  // DELETE
        1,  // SCHEMA
        0,  // PRIVILEGES
        1,  // OP_EQUAL
        1,  // OP_NOT
        1,  // OP_GREATER
        1,  // OP_SMALLER
        1,  // OP_BETWEEN
        1,  // OP_LIKE
        1,  // OP_NULL
        0,  // SELECT_FOR_UPDATE
        0,  // START_QUERY
        0,  // END_QUERY
        0,  // UNION_CONDLIST
        1,  // CREATE_TABLE
        1,  // DROP_TABLE
        0,  // CREATE_INDEX
        0,  // DROP_INDEX
        0,  // PROCEDURE
        0,  // CREATE_VIEW
        0,  // DROP_VIEW
        0,  // QUERY_VIEW
        0,  // CREATE_USER
        0,  // DROP_USER
        0,  // CREATE_ROLE
        0,  // DROP_ROLE
        0,  // GRANT
        0,  // REVOKE
        0,  // PASSTHROUGH_QUERY
        0,  // NATIVE_COMMAND
        0,  // ALTER_TABLE
        0,  // BLOCK_JOIN
        0,  // XA
        0,  // QUERY_MODE_SELECTION
        0,  // VALIDATE_SCHEMAOBJECTS_IN_USE
        1,  // UNICODE_INFO
        0,0,0,0,0,0,0,0 // padding
    };

    public damip() { m_tmHandle = 0; }

    // --------------------- Lifecycle ----------------------------------------

    @Override
    public int ipConnect(long tmHandle, long dam_hdbc, String sDataSourceName,
                         String sUserName, String sPassword,
                         String sCurrentCatalog, String sIPProperties, String sIPCustomProperties) {
        m_tmHandle = tmHandle;
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipConnect called\n");

        final String excelFilePath = parseExcelPath(sIPProperties, sIPCustomProperties);
        if (excelFilePath == null || excelFilePath.isEmpty()) {
            jdam.dam_addError(0, dam_hdbc, DAM_IP_ERROR, 1, "ExcelFile property not provided");
            return IP_FAILURE;
        }

        try {
            this.conn = new ExcelConnection(excelFilePath, m_tmHandle);
            return IP_SUCCESS;
        } catch (ExcelInitException e) {
            jdam.dam_addError(0, dam_hdbc, DAM_IP_ERROR, e.code, e.getMessage());
            return IP_FAILURE;
        }
    }

    @Override
    public int ipDisconnect(long dam_hdbc) {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipDisconnect called\n");
        if (conn != null) {
            if (inTransaction) conn.save();
            conn.close();
            conn = null;
        }
        return IP_SUCCESS;
    }

    @Override
    public int ipStartTransaction(long dam_hdbc) {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipStartTransaction called\n");
        inTransaction = true;
        return IP_SUCCESS;
    }

    @Override
    public int ipEndTransaction(long dam_hdbc, int iType) {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipEndTransaction called\n");
        if (!ensureConnected(dam_hdbc)) return IP_FAILURE;
        if (iType == DAM_COMMIT) {
            conn.save();
        } else if (iType == DAM_ROLLBACK) {
            jdam.trace(m_tmHandle, UL_TM_INFO, "Rolling back changes\n");
            conn.reload();
        }
        inTransaction = false;
        return IP_SUCCESS;
    }

    // --------------------- Capability/Info ----------------------------------

    @Override
    public int ipGetSupport(int iSupportType) {
        if (iSupportType >= 0 && iSupportType < ip_support_array.length) return ip_support_array[iSupportType];
        return 0;
    }

    @Override
    public String ipGetInfo(int iInfoType) {
        switch (iInfoType) {
            case IP_INFO_QUALIFIER_TERMW: return "database";
            case IP_INFO_OWNER_TERMW:     return "owner";
            case IP_INFO_QUALIFIER_NAMEW:
                if (conn != null && conn.excelFilePath != null) {
                    String fileName = conn.excelFilePath;
                    int slash = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
                    if (slash >= 0 && slash < fileName.length() - 1) fileName = fileName.substring(slash + 1);
                    return fileName;
                }
                return null;
            case IP_INFO_OWNER_NAMEW: return OA_SCHEMA_NAME;
            case IP_INFO_SUPPORT_SCHEMA_SEARCH_PATTERN:
            case IP_INFO_SUPPORT_VALUE_FOR_RESULT_ALIAS:
            case IP_INFO_VALIDATE_TABLE_WITH_OWNER: return "0";
            case IP_INFO_FILTER_VIEWS_WITH_QUALIFIER_NAME:
            case IP_INFO_CONVERT_NUMERIC_VAL:
            case IP_INFO_TABLE_ROWSET_REPORT_MEMSIZE_LIMIT: return "1";
            default:
                jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipGetInfo: Unknown info type " + iInfoType + "\n");
                return null;
        }
    }

    @Override
    public int ipSetInfo(int iInfoType, String infoVal) {
        if (iInfoType == IP_INFO_QUALIFIER_NAMEW) {
            jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipSetInfo: qualifier set to " + infoVal + "\n");
            return IP_SUCCESS;
        }
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipSetInfo: InfoType " + iInfoType + " not used\n");
        return IP_SUCCESS;
    }

    // --------------------- Schema discovery ---------------------------------

    @Override
    public int ipSchema(long dam_hdbc, long pMemTree, int iType, long pList, Object pSearchObj) {
        if (!ensureConnected(dam_hdbc)) return IP_FAILURE;

        switch (iType) {
            case DAMOBJ_TYPE_CATALOG: {
                schemaobj_table catObj = new schemaobj_table(OA_CATALOG_NAME, null, null, null, null, null, null, null);
                jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, catObj);
                break;
            }
            case DAMOBJ_TYPE_SCHEMA: {
                schemaobj_table schObj = new schemaobj_table();
                schObj.SetObjInfo(null, "SYSTEM", null, null, null, null, null, null);
                jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, schObj);
                schObj.SetObjInfo(null, OA_SCHEMA_NAME, null, null, null, null, null, null);
                jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, schObj);
                break;
            }
            case DAMOBJ_TYPE_TABLETYPE: {
                schemaobj_table typeObj = new schemaobj_table();
                typeObj.SetObjInfo(null, null, null, "TABLE", null, null, null, null);
                jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, typeObj);
                break;
            }
            case DAMOBJ_TYPE_TABLE: {
                schemaobj_table tblSearch = (schemaobj_table) pSearchObj;
                for (TableMeta table : conn.schema.getTables()) {
                    if (matchesTableFilter(tblSearch, table.name)) {
                        schemaobj_table tblObj = new schemaobj_table();
                        tblObj.SetObjInfo(OA_CATALOG_NAME, OA_SCHEMA_NAME, table.name, "TABLE",
                                          null, null, null, table.name + " Table");
                        jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, tblObj);
                    }
                }
                break;
            }
            case DAMOBJ_TYPE_COLUMN: {
                schemaobj_column colSearch = (schemaobj_column) pSearchObj;
                for (TableMeta table : conn.schema.getTables()) {
                    if (matchesTableFilter(colSearch, table.name)) {
                        for (ColumnMeta col : table.columns) {
                            if (colSearch == null || colSearch.getColumnName() == null
                                || jdam.dam_strlikecmp(colSearch.getColumnName(), col.name) == 0) {
                                schemaobj_column colObj = new schemaobj_column();
                                short sqlTypeCode  = conn.schema.mapTypeNameToCode(col.sqlType);
                                short nullableFlag = (short)(col.isNullable ? XO_NULLABLE : XO_NO_NULLS);
                                colObj.SetObjInfo(OA_CATALOG_NAME, OA_SCHEMA_NAME, table.name, col.name,
                                                  sqlTypeCode, col.sqlType, col.precision, col.scale,
                                                  (short)DAMOBJ_NOTSET, (short)0,
                                                  nullableFlag, (short)DAMOBJ_NOTSET,
                                                  null, null, (short)SQL_PC_NOT_PSEUDO, (short)0,
                                                  col.name);
                                jdam.dam_add_schemaobj(pMemTree, iType, pList, pSearchObj, colObj);
                            }
                        }
                    }
                }
                break;
            }
            default: break;
        }
        return IP_SUCCESS;
    }

    // --------------------- DDL ----------------------------------------------

    @Override
    public int ipDDL(long dam_hstmt, int iStmtType, xo_long piNumResRows) {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipDDL called\n");
        if (!ensureConnected(dam_hstmt)) return IP_FAILURE;

        StringBuffer sbTable = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);

        if (iStmtType == DAM_CREATE_TABLE) {
            // Target table name
            jdam.dam_describeTable(dam_hstmt, null, null, sbTable, null, null);
            String newTableName = sbTable.toString();
            jdam.trace(m_tmHandle, UL_TM_INFO, "Creating table: " + newTableName + "\n");

            if (conn.schema.hasTable(newTableName)) {
                jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 3,
                        "Table '" + newTableName + "' already exists");
                return IP_FAILURE;
            }

            // Get schema object list for columns defined in this CREATE TABLE
            long colList = jdam.dam_getSchemaObjectList(dam_hstmt, DAMOBJ_TYPE_COLUMN);
            Object obj = jdam.dam_getFirstSchemaObject(colList);
            if (obj == null) {
                jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 4,
                        "No columns specified for table " + newTableName);
                return IP_FAILURE;
            }

            TableMeta newTable = new TableMeta(newTableName);

            while (obj != null) {
                schemaobj_column scol = (schemaobj_column) obj;

                // Prepare holders following the SetObjInfo field order (all others may be null)
                StringBuffer outQualifier  = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
                StringBuffer outOwner      = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
                StringBuffer outTblName    = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
                StringBuffer outColName    = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
                xo_short      dataTypeCode   = new xo_short((short) 0);   // short in spec; xo_int holder is supported
                StringBuffer typeNameBuf   = new StringBuffer(128);
                xo_int      charMaxLen     = new xo_int(0);
                xo_int      numPrecision   = new xo_int(0);
                xo_short      numPrecRadix   = new xo_short((short) 0);
                xo_short      numScale       = new xo_short((short) 0);
                xo_short      nullable       = new xo_short((short) 0);
                xo_short      scope          = new xo_short((short) 0);
                StringBuffer userDataBuf   = new StringBuffer(256);
                StringBuffer opSupportBuf  = new StringBuffer(256);
                xo_short      pseudoColumn   = new xo_short((short) 0);
                xo_short      columnType     = new xo_short((short) 0);
                StringBuffer remarksBuf    = new StringBuffer(256);

                // Pull everything the engine parsed for this column
                // (Any outputs you do not need can be passed as null.)
                try {
                    scol.GetObjInfo(
                        outQualifier,
                        outOwner,
                        outTblName,
                        outColName,
                        dataTypeCode,
                        typeNameBuf,
                        charMaxLen,
                        numPrecision,
                        numPrecRadix,
                        numScale,
                        nullable,
                        scope,
                        userDataBuf,
                        opSupportBuf,
                        pseudoColumn,
                        columnType,
                        remarksBuf
                    );
                } catch (Throwable t) {
                    jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 4,
                            "Failed to read column metadata: " + t.getMessage());
                    return IP_FAILURE;
                }

                String colName = outColName.toString();
                if (colName == null || colName.isEmpty()) {
                    jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 4,
                            "Column with no name in CREATE TABLE " + newTableName);
                    return IP_FAILURE;
                }

                // Decide final type/precision/scale
                short dtCode = (short) dataTypeCode.getVal();
                String typeName = typeNameBuf.length() > 0
                        ? typeNameBuf.toString()
                        : conn.schema.mapTypeCodeToName(dtCode); // map when engine didn't provide a name

                int precision;
                int scale;

                // If character max length present, treat as length for character types
                if (charMaxLen.getVal() > 0) {
                    precision = charMaxLen.getVal();
                    scale = 0;
                } else {
                    precision = Math.max(0, numPrecision.getVal());
                    scale     = Math.max(0, numScale.getVal());
                }

                boolean isNullable = (nullable.getVal() == XO_NULLABLE);

                newTable.columns.add(new ColumnMeta(colName, typeName, precision, scale, isNullable));

                obj = jdam.dam_getNextSchemaObject(colList);
            }

            // Create the sheet and persist schema
            if (!conn.excelHelper.createSheet(newTableName, newTable.getColumnNames())) {
                jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 5,
                        "Failed to create sheet for table " + newTableName);
                return IP_FAILURE;
            }

            conn.schema.addTable(newTable);
            if (!inTransaction) conn.save();
            piNumResRows.setVal(0);
            return IP_SUCCESS;
        }
        else if (iStmtType == DAM_DROP_TABLE) {
            jdam.dam_describeTable(dam_hstmt, null, null, sbTable, null, null);
            String dropTableName = sbTable.toString();
            jdam.trace(m_tmHandle, UL_TM_INFO, "Dropping table: " + dropTableName + "\n");

            if (!conn.schema.hasTable(dropTableName)) {
                jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 6,
                        "Table '" + dropTableName + "' does not exist");
                return IP_FAILURE;
            }
            if (!conn.excelHelper.deleteSheet(dropTableName)) {
                jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 7,
                        "Failed to drop sheet for table " + dropTableName);
                return IP_FAILURE;
            }
            conn.schema.removeTable(dropTableName);
            if (!inTransaction) conn.save();
            piNumResRows.setVal(0);
            return IP_SUCCESS;
        }

        jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 8, "Unsupported DDL statement");
        return IP_FAILURE;
    }




    // --------------------- DML / Query --------------------------------------

    @Override
    public int ipExecute(long dam_hstmt, int iStmtType, long hSearchCol, xo_long piNumResRows) {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipExecute called\n");
        if (!ensureConnected(dam_hstmt)) return IP_FAILURE;
        piNumResRows.setVal(0);

        String tableName = null;
        if (iStmtType == DAM_SELECT || iStmtType == DAM_INSERT || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE) {
            StringBuffer sbTable = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
            jdam.dam_describeTable(dam_hstmt, null, null, sbTable, null, null);
            tableName = sbTable.toString();
            if (!conn.schema.hasTable(tableName)) {
                jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 9, "Table '" + tableName + "' is not defined in schema");
                return IP_FAILURE;
            }
        }

        switch (iStmtType) {
            case DAM_SELECT: {
                TableMeta table = conn.schema.getTable(tableName);
                int rowCount = conn.excelHelper.getRowCount(tableName);
                for (int rowIdx = 0; rowIdx < rowCount; ++rowIdx) {
                    RowData rowData = conn.excelHelper.readRow(tableName, rowIdx);
                    if (rowData == null) continue;
                    long hRow = jdam.dam_allocRow(dam_hstmt);
                    boolean rowOK = true;

                    for (int colIdx = 0; colIdx < table.columns.size(); ++colIdx) {
                        ColumnMeta colMeta = table.columns.get(colIdx);
                        Object value = rowData.getValue(colIdx);
                        int ret = (value == null)
                                ? addNullToRow(dam_hstmt, hRow, jdam.dam_getCol(dam_hstmt, colMeta.name), colMeta.sqlType)
                                : addValueToRow(dam_hstmt, hRow, colMeta, value);
                        if (ret != DAM_SUCCESS) { rowOK = false; break; }
                    }

                    if (!rowOK) { jdam.dam_freeRow(hRow); continue; }
                    if (jdam.dam_isTargetRow(dam_hstmt, hRow) == DAM_TRUE) {
                        jdam.dam_addRowToTable(dam_hstmt, hRow);
                        piNumResRows.setVal(piNumResRows.getVal() + 1);
                    } else {
                        jdam.dam_freeRow(hRow);
                    }
                }
                return IP_SUCCESS;
            }

            case DAM_FETCH:
                return IP_SUCCESS;

            case DAM_INSERT: {
                long hInsRow = jdam.dam_getFirstInsertRow(dam_hstmt);
                int insertCount = 0;
                while (hInsRow != 0) {
                    int newRowIdx = conn.excelHelper.addEmptyRow(tableName);
                    if (newRowIdx < 0) {
                        jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 10, "Failed to insert new row into " + tableName);
                        return IP_FAILURE;
                    }
                    long hVal = jdam.dam_getFirstValueSet(dam_hstmt, hInsRow);
                    while (hVal != 0) {
                        long hColToSet = jdam.dam_getColToSet(hVal);
                        StringBuffer colNameBuf = new StringBuffer();
                        jdam.dam_describeCol(hColToSet, null, colNameBuf, null, null);
                        String colName = colNameBuf.toString();
                        ColumnMeta colMeta = conn.schema.getTable(tableName).getColumn(colName);
                        Object newVal = jdam.dam_getValueToSet(hVal, getXOTypeFor(colMeta.sqlType), new xo_int(1));
                        conn.excelHelper.setCell(tableName, newRowIdx, colMeta, newVal);
                        hVal = jdam.dam_getNextValueSet(dam_hstmt);
                    }
                    insertCount++;
                    hInsRow = jdam.dam_getNextInsertRow(dam_hstmt);
                }
                piNumResRows.setVal(insertCount);
                if (!inTransaction) conn.save();
                return IP_SUCCESS;
            }

            case DAM_UPDATE: {
                TableMeta updTable = conn.schema.getTable(tableName);
                int updateCount = 0;
                for (int r = 0; r < conn.excelHelper.getRowCount(tableName); ++r) {
                    RowData rowData = conn.excelHelper.readRow(tableName, r);
                    if (rowData == null) continue;
                    long hTmpRow = jdam.dam_allocRow(dam_hstmt);
                    boolean ok = true;

                    for (int c = 0; c < updTable.columns.size(); ++c) {
                        ColumnMeta colMeta = updTable.columns.get(c);
                        Object val = rowData.getValue(c);
                        int res = (val == null)
                                ? addNullToRow(dam_hstmt, hTmpRow, jdam.dam_getCol(dam_hstmt, colMeta.name), colMeta.sqlType)
                                : addValueToRow(dam_hstmt, hTmpRow, colMeta, val);
                        if (res != DAM_SUCCESS) { ok = false; break; }
                    }
                    if (!ok) { jdam.dam_freeRow(hTmpRow); continue; }

                    if (jdam.dam_isTargetRow(dam_hstmt, hTmpRow) == DAM_TRUE) {
                        long hUpdRow = jdam.dam_getUpdateRow(dam_hstmt, hTmpRow);
                        long hValSet = jdam.dam_getFirstValueSet(dam_hstmt, hUpdRow);
                        while (hValSet != 0) {
                            long hColToSet = jdam.dam_getColToSet(hValSet);
                            StringBuffer colNameBuf = new StringBuffer();
                            jdam.dam_describeCol(hColToSet, null, colNameBuf, null, null);
                            String colName = colNameBuf.toString();
                            ColumnMeta colMeta = updTable.getColumn(colName);
                            Object newVal = jdam.dam_getValueToSet(hValSet, getXOTypeFor(colMeta.sqlType), new xo_int(1));
                            conn.excelHelper.setCell(tableName, r, colMeta, newVal);
                            hValSet = jdam.dam_getNextValueSet(dam_hstmt);
                        }
                        updateCount++;
                    }
                    jdam.dam_freeRow(hTmpRow);
                }
                piNumResRows.setVal(updateCount);
                if (!inTransaction) conn.save();
                return IP_SUCCESS;
            }

            case DAM_DELETE: {
                TableMeta delTable = conn.schema.getTable(tableName);
                int deleteCount = 0;
                for (int r = conn.excelHelper.getRowCount(tableName) - 1; r >= 0; --r) {
                    RowData rowData = conn.excelHelper.readRow(tableName, r);
                    if (rowData == null) continue;
                    long hTmpRow = jdam.dam_allocRow(dam_hstmt);
                    boolean ok = true;

                    for (int c = 0; c < delTable.columns.size(); ++c) {
                        ColumnMeta colMeta = delTable.columns.get(c);
                        Object val = rowData.getValue(c);
                        int res = (val == null)
                                ? addNullToRow(dam_hstmt, hTmpRow, jdam.dam_getCol(dam_hstmt, colMeta.name), colMeta.sqlType)
                                : addValueToRow(dam_hstmt, hTmpRow, colMeta, val);
                        if (res != DAM_SUCCESS) { ok = false; break; }
                    }
                    if (!ok) { jdam.dam_freeRow(hTmpRow); continue; }

                    if (jdam.dam_isTargetRow(dam_hstmt, hTmpRow) == DAM_TRUE) {
                        conn.excelHelper.deleteRow(tableName, r);
                        deleteCount++;
                    }
                    jdam.dam_freeRow(hTmpRow);
                }
                piNumResRows.setVal(deleteCount);
                if (!inTransaction) conn.save();
                return IP_SUCCESS;
            }

            case DAM_CLOSE:
                return IP_SUCCESS;

            default:
                return IP_FAILURE;
        }
    }

    // --------------------- Not supported sections ---------------------------

    @Override
    public int ipProcedure(long dam_hstmt, int iType, xo_long piNumResRows) {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipProcedure called (not supported)\n");
        return IP_FAILURE;
    }
    @Override
    public int ipDCL(long dam_hstmt, int iStmtType, xo_long piNumResRows) {
        jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipDCL called (not supported)\n");
        return IP_FAILURE;
    }
    @Override
    public int ipPrivilege(int iStmtType, String user, String catalog, String schema, String objName) {
        return IP_FAILURE;
    }
    @Override
    public int ipNative(long dam_hstmt, int iCommandOption, String sCommand, xo_long piNumResRows) {
        return IP_FAILURE;
    }
    @Override
    public int ipSchemaEx(long dam_hstmt, long pMemTree, int iType, long pList, Object pSearchObj) {
        return IP_FAILURE;
    }
    @Override
    public int ipProcedureDynamic(long dam_hstmt, int iType, xo_long piNumResRows) {
        return IP_FAILURE;
    }

    // --------------------- Helpers ------------------------------------------

    private String parseExcelPath(String sIPProps, String sIPCustomProps) {
        String props = (sIPProps != null ? sIPProps : "") + ";" + (sIPCustomProps != null ? sIPCustomProps : "");
        for (String prop : props.split(";")) {
            String p = prop.trim();
            if (p.toLowerCase().startsWith("excelfile=")) {
                return p.substring("excelfile=".length());
            }
        }
        return null;
    }

    private boolean ensureConnected(long errHandle) {
        if (conn == null) {
            jdam.dam_addError(0, errHandle, DAM_IP_ERROR, 100, "Not connected. Call ipConnect first.");
            return false;
        }
        return true;
    }

    private boolean matchesTableFilter(schemaobj_table searchObj, String tableName) {
        if (searchObj == null) return true;
        if (searchObj.getTableQualifier() != null && !searchObj.getTableQualifier().equalsIgnoreCase(OA_CATALOG_NAME))
            return false;
        if (searchObj.getTableOwner() != null && !searchObj.getTableOwner().equalsIgnoreCase(OA_SCHEMA_NAME))
            return false;
        String searchName = searchObj.getTableName();
        if (searchName == null) return true;
        return jdam.dam_strlikecmp(searchName, tableName) == 0;
    }

    private boolean matchesTableFilter(schemaobj_column searchObj, String tableName) {
        if (searchObj == null) return true;
        if (searchObj.getTableQualifier() != null && !searchObj.getTableQualifier().equalsIgnoreCase(OA_CATALOG_NAME))
            return false;
        if (searchObj.getTableOwner() != null && !searchObj.getTableOwner().equalsIgnoreCase(OA_SCHEMA_NAME))
            return false;
        String searchTable = searchObj.getTableName();
        if (searchTable == null) return true;
        return jdam.dam_strlikecmp(searchTable, tableName) == 0;
    }

    private int getXOTypeFor(String sqlType) {
        switch(sqlType.toUpperCase()) {
            case "INTEGER": return XO_TYPE_INTEGER;
            case "SMALLINT": return XO_TYPE_SMALLINT;
            case "TINYINT": return XO_TYPE_TINYINT;
            case "BIGINT": return XO_TYPE_BIGINT;
            case "DOUBLE":
            case "FLOAT": return XO_TYPE_DOUBLE;
            case "REAL": return XO_TYPE_REAL;
            case "NUMERIC":
            case "DECIMAL": return XO_TYPE_DECIMAL;
            case "CHAR":
            case "VARCHAR":
            case "LONGVARCHAR": return XO_TYPE_CHAR;
            case "WCHAR":
            case "WVARCHAR":
            case "WLONGVARCHAR": return XO_TYPE_WCHAR;
            case "DATE": return XO_TYPE_DATE;
            case "TIME": return XO_TYPE_TIME;
            case "TIMESTAMP": return XO_TYPE_TIMESTAMP;
            case "BIT": return XO_TYPE_BIT;
            default: return XO_TYPE_CHAR;
        }
    }

    private int addValueToRow(long hstmt, long hRow, ColumnMeta colMeta, Object value) {
        long hCol = jdam.dam_getCol(hstmt, colMeta.name);
        String type = colMeta.sqlType.toUpperCase();
        try {
            if (value == null) {
                return addNullToRow(hstmt, hRow, hCol, colMeta.sqlType);
            }
            switch(type) {
                case "INTEGER":
                case "SMALLINT":
                case "TINYINT":
                case "BIGINT": {
                    long lv = (value instanceof Number) ? ((Number)value).longValue() : Long.parseLong(value.toString());
                    return "BIGINT".equals(type)
                            ? jdam.dam_addBigIntValToRow(hstmt, hRow, hCol, lv, 0)
                            : jdam.dam_addIntValToRow(hstmt, hRow, hCol, (int) lv, 0);
                }
                case "DOUBLE":
                case "FLOAT":
                case "REAL":
                case "DECIMAL":
                case "NUMERIC": {
                    double dv = (value instanceof Number) ? ((Number)value).doubleValue() : Double.parseDouble(value.toString());
                    return jdam.dam_addDoubleValToRow(hstmt, hRow, hCol, dv, 0);
                }
                case "CHAR":
                case "VARCHAR":
                case "LONGVARCHAR":
                case "WCHAR":
                case "WVARCHAR":
                case "WLONGVARCHAR":
                    return jdam.dam_addCharValToRow(hstmt, hRow, hCol, value.toString(), XO_NTS);
                case "BIT": {
                    boolean bv = (value instanceof Boolean) ? (Boolean) value
                                : "true".equalsIgnoreCase(value.toString())
                               || "1".equals(value.toString())
                               || "yes".equalsIgnoreCase(value.toString());
                    return jdam.dam_addBitValToRow(hstmt, hRow, hCol, bv, 0);
                }
                case "DATE":
                case "TIME":
                case "TIMESTAMP": {
                    Date dateVal;
                    if (value instanceof Date) {
                        dateVal = (Date) value;
                    } else if (value instanceof Number) {
                        dateVal = DateUtil.getJavaDate(((Number)value).doubleValue());
                    } else {
                        dateVal = javax.xml.bind.DatatypeConverter.parseDateTime(value.toString()).getTime();
                    }
                    xo_tm xoTime = new xo_tm();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateVal);
                    xoTime.setVal(xo_tm.YEAR,   cal.get(Calendar.YEAR));
                    xoTime.setVal(xo_tm.MONTH,  cal.get(Calendar.MONTH));
                    xoTime.setVal(xo_tm.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                    xoTime.setVal(xo_tm.HOUR,   cal.get(Calendar.HOUR_OF_DAY));
                    xoTime.setVal(xo_tm.MINUTE, cal.get(Calendar.MINUTE));
                    xoTime.setVal(xo_tm.SECOND, cal.get(Calendar.SECOND));
                    xoTime.setVal(xo_tm.FRACTION, cal.get(Calendar.MILLISECOND) * 1_000_000);
                    if ("DATE".equals(type)) {
                        xoTime.setVal(xo_tm.HOUR, 0);
                        xoTime.setVal(xo_tm.MINUTE, 0);
                        xoTime.setVal(xo_tm.SECOND, 0);
                        xoTime.setVal(xo_tm.FRACTION, 0);
                    }
                    return jdam.dam_addTimeStampValToRow(hstmt, hRow, hCol, xoTime, 0);
                }
                default:
                    return jdam.dam_addCharValToRow(hstmt, hRow, hCol, value.toString(), XO_NTS);
            }
        } catch (Exception e) {
            jdam.trace(m_tmHandle, UL_TM_ERRORS, "Error converting value for column " + colMeta.name + ": " + e + "\n");
            return DAM_FAILURE;
        }
    }

    private int addNullToRow(long hstmt, long hRow, long hCol, String sqlTypeName) {
        String t = (sqlTypeName == null) ? "VARCHAR" : sqlTypeName.toUpperCase();
        switch (t) {
            case "INTEGER":
            case "SMALLINT":
            case "TINYINT":   return jdam.dam_addIntValToRow(hstmt, hRow, hCol, 0, XO_NULL_DATA);
            case "BIGINT":    return jdam.dam_addBigIntValToRow(hstmt, hRow, hCol, 0L, XO_NULL_DATA);
            case "DOUBLE":
            case "FLOAT":
            case "REAL":
            case "DECIMAL":
            case "NUMERIC":   return jdam.dam_addDoubleValToRow(hstmt, hRow, hCol, 0.0, XO_NULL_DATA);
            case "BIT":       return jdam.dam_addBitValToRow(hstmt, hRow, hCol, false, XO_NULL_DATA);
            case "DATE":
            case "TIME":
            case "TIMESTAMP": return jdam.dam_addTimeStampValToRow(hstmt, hRow, hCol, new xo_tm(), XO_NULL_DATA);
            case "WCHAR":
            case "WVARCHAR":
            case "WLONGVARCHAR":
            default:          return jdam.dam_addCharValToRow(hstmt, hRow, hCol, "", XO_NULL_DATA);
        }
    }
}
