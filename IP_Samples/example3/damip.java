/* damip.java
 *
 * Copyright (c) 1995-2013 Progress Software Corporation. All Rights Reserved.
 *
 *
 *
 * Description:     This example IP
 *                  - is implemented in "JAVA"
 *                  - supports dynamic schema
 *                  - supports single column index on column NAME
 *                  - supports SELECT, INSERT, UPDATE & DELETE operations
 */

package oajava.example3;

import java.util.Date;
import java.util.Calendar;
import oajava.sql.*;
/* define the damip class to implement the sample IP */
public class damip implements oajava.sql.ip
{
    private long m_tmHandle = 0;
    private long m_iNumResRows;
    private xo_int iCurrentRow;
    private StringBuffer    sTableName; /* Name of the table being queried (char [DAM_MAX_ID_LEN+1] )*/
    private static mdb m_pMdb = null;
    private static boolean  bAllowSchemaSearchPattern = false;

    /* connection information */
    private StringBuffer    m_sQualifier;         /* char [DAM_MAX_ID_LEN+1] */
    private StringBuffer    m_sUserName;          /* char [DAM_MAX_ID_LEN+1] */
    private long iFetchSize;  /* records to return in cursor mode */

    /* index information */
    private StringBuffer    sIndexName;         /* char [DAM_MAX_ID_LEN+1] */
    private xo_long         hindex;             /* DAM_HINDEX */
    private xo_long         hset_of_condlist;   /* DAM_HSET_OF_CONDLIST */

    /* column handles */
    private long hcolName, hcolIntVal, hcolDoubleVal, hcolTime; /* DAM_HCOL */

    final static String OA_CATALOG_NAME   = "SCHEMA";        /* SCHEMA */
    final static String OA_USER_NAME      = "OAUSER";        /* OAUSER */

    /* Support array */
    private final int[]   ip_support_array =
                    {
                        0,
                        1, /* IP_SUPPORT_SELECT */
                        1, /* IP_SUPPORT_INSERT */
                        1, /* IP_SUPPORT_UPDATE */
                        1, /* IP_SUPPORT_DELETE */
                        1, /* IP_SUPPORT_SCHEMA - IP supports Schema Functions */
                        0, /* IP_SUPPORT_PRIVILEGES  */
                        1, /* IP_SUPPORT_OP_EQUAL */
                        0, /* IP_SUPPORT_OP_NOT   */
                        0, /* IP_SUPPORT_OP_GREATER */
                        0, /* IP_SUPPORT_OP_SMALLER */
                        0, /* IP_SUPPORT_OP_BETWEEN */
                        0, /* IP_SUPPORT_OP_LIKE    */
                        0, /* IP_SUPPORT_OP_NULL    */
                        0, /* IP_SUPPORT_SELECT_FOR_UPDATE */
                        0, /* IP_SUPPORT_START_QUERY */
                        0, /* IP_SUPPORT_END_QUERY */
                        0, /* IP_SUPPORT_UNION_CONDLIST */
                        0, /* IP_SUPPORT_CREATE_TABLE */
                        0, /* IP_SUPPORT_DROP_TABLE */
                        0, /* IP_SUPPORT_CREATE_INDEX */
                        0, /* IP_SUPPORT_DROP_INDEX */
                        0, /* IP_SUPPORT_PROCEDURE */
                        0, /* IP_SUPPORT_CREATE_VIEW */
                        0, /* IP_SUPPORT_DROP_VIEW */
                        0, /* IP_SUPPORT_QUERY_VIEW */
                        0, /* IP_SUPPORT_CREATE_USER */
                        0, /* IP_SUPPORT_DROP_USER */
                        0, /* IP_SUPPORT_CREATE_ROLE */
                        0, /* IP_SUPPORT_DROP_ROLE */
                        0, /* IP_SUPPORT_GRANT */
                        0, /* IP_SUPPORT_REVOKE */
                        0,  /* IP_SUPPORT_PASSTHROUGH_QUERY */
                        0,  /* IP_SUPPORT_NATIVE_COMMAND */
                        0,  /* IP_SUPPORT_ALTER_TABLE */
                        0,  /* IP_SUPPORT_BLOCK_JOIN */
                        0,  /* IP_SUPPORT_XA */
                        0,  /* IP_SUPPORT_QUERY_MODE_SELECTION */
                        0,  /* IP_SUPPORT_VALIDATE_SCHEMAOBJECTS_IN_USE */
                        1,  /* IP_SUPPORT_UNICODE_INFO */
                        0,  /* IP_SUPPORT_JOIN_ORDER_SELECTION */
                        0,  /* IP_SUPPORT_TABLE_FUNCTIONS */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
                        0,  /* Reserved for future use */
                        0   /* Reserved for future use */
                    };


    public damip()
    {
            m_tmHandle = 0;
            m_iNumResRows = 0;
            iCurrentRow = new xo_int(0);

            m_sQualifier = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
            m_sUserName = new StringBuffer(ip.DAM_MAX_ID_LEN+1);
            sTableName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
            sIndexName = new StringBuffer(ip.DAM_MAX_ID_LEN + 1);
            hcolName = 0;
            hcolIntVal = 0;
            hcolDoubleVal = 0;
            hcolTime = 0;
            hindex= new xo_long(0);
            hset_of_condlist = new xo_long(0);

			synchronized(getClass())
			{
                /*create one instance of the mdb object */
                if(m_pMdb == null)
                    m_pMdb = new mdb();
            }
        }

    public String ipGetInfo(int iInfoType)
    {
        String str = null;
        switch (iInfoType)
                {
                    case IP_INFO_QUALIFIER_TERMW:
                        /* return the Qualifier term used in Schema information */
                        str = "database";
                        break;

                    case IP_INFO_OWNER_TERMW:
                        /* return the Owner term used in Schema information */
                        str = "owner";
                        break;

                    case IP_INFO_QUALIFIER_NAMEW:
                        str = m_sQualifier.toString();
                        break;

                    case IP_INFO_OWNER_NAMEW:
                        /* we need to return information that matches schema */
                        str = m_sUserName.toString();
                        break;

                    case IP_INFO_SUPPORT_SCHEMA_SEARCH_PATTERN:
                        str = (!bAllowSchemaSearchPattern) ? "0" : "1"; /* false/true */
                        break;

                    case IP_INFO_SUPPORT_VALUE_FOR_RESULT_ALIAS:
                    case IP_INFO_VALIDATE_TABLE_WITH_OWNER:
                        str = "0"; /* false */
                        break;

                    case IP_INFO_FILTER_VIEWS_WITH_QUALIFIER_NAME:
                    case IP_INFO_CONVERT_NUMERIC_VAL:
                    case IP_INFO_TABLE_ROWSET_REPORT_MEMSIZE_LIMIT:
                        str = "1"; /* true */
                        break;
                    default:
                    	jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipGetInfo(): Information type:"+ iInfoType +" is out of range\n");
                        break;
                }
                return str;
        }

    public int ipSetInfo(int iInfoType,String InfoVal)
    {
        switch(iInfoType)
        {
            case IP_INFO_QUALIFIER_NAMEW:
                m_sQualifier.delete(0, m_sQualifier.length());
                m_sQualifier.append(InfoVal);
                break;
            default:
            	jdam.trace(m_tmHandle, UL_TM_F_TRACE, "ipSetInfo(): Information type:"+ iInfoType +" is out of range\n");
                return DAM_NOT_AVAILABLE;
        }

        return IP_SUCCESS;
    }

    public int ipGetSupport(int iSupportType)
    {
        return(ip_support_array[iSupportType]);
    }

        /*ipConnect is called immediately after an instance of this object is created. You should
         *perform any tasks related to connecting to your data source */
    public int ipConnect(long tmHandle,long dam_hdbc,String sDataSourceName, String sUserName, String sPassword,
						String sCurrentCatalog, String sIPProperties, String sIPCustomProperties)
        {
            /* Save the trace handle */
            m_tmHandle = tmHandle;
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipConnect called\n");

            m_sQualifier.delete(0, m_sQualifier.length());
            m_sQualifier.append(OA_CATALOG_NAME);
            m_sUserName.delete(0, m_sUserName.length());
            m_sUserName.append(OA_USER_NAME);
            /* m_sUserName.append(sUserName); */

            /* Code to connect to your data source source.
               for our sample MDB database no need to connect */
            return IP_SUCCESS;
        }

    public int ipDisconnect(long dam_hdbc)
    {   /* disconnect from the data source */
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipDisonnect called\n");
            return IP_SUCCESS;
    }

    public int ipStartTransaction(long dam_hdbc)
    {
            /* start a new transaction */
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipStartTransaction called\n");
            return IP_SUCCESS;
    }

    public int ipEndTransaction(long dam_hdbc,int iType)
    {
            /* end the transaction */
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipEndTransaction called\n");
            if (iType == DAM_COMMIT)
            {
            }
            else if (iType == DAM_ROLLBACK)
            {
            }
            return IP_SUCCESS;
    }

    public int ipExecute(long dam_hstmt, int iStmtType, long hSearchCol,xo_long piNumResRows)
        {
            int iRetCode;
            jdam.trace(m_tmHandle, UL_TM_F_TRACE,"ipExecute called\n");

            if (iStmtType == DAM_SELECT || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE || iStmtType == DAM_INSERT)
            {
                /* get the table information */
                jdam.dam_describeTable(dam_hstmt, null, null, sTableName,null, null);
                /* get the column handles */
                hcolName = jdam.dam_getCol(dam_hstmt,"NAME");
                hcolIntVal = jdam.dam_getCol(dam_hstmt,"INTVAL");
                hcolDoubleVal = jdam.dam_getCol(dam_hstmt,"FLOATVAL");
                hcolTime = jdam.dam_getCol(dam_hstmt,"TIME");

                hindex.setVal(0);

                if (iStmtType == DAM_SELECT)
                {
                    xo_int  piValue;

                    piValue = new xo_int();

                    /* get fetch block size */
                    iRetCode = jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_FETCH_BLOCK_SIZE,
					                                                      null, piValue);
                    if (iRetCode != DAM_SUCCESS) iFetchSize = 2;
                    else iFetchSize = piValue.getVal();
                    jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Fetch Block Size is set to " + iFetchSize + "rows.\n");
                }
            }
            /* initialize the result */
            m_iNumResRows = 0;
            /* process the query based on the type */
            if (iStmtType == DAM_SELECT || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE)
            {
                /* get the index information and process the query*/
                iRetCode = jdam.dam_getOptimalIndexAndConditions(dam_hstmt, hindex, hset_of_condlist);
                if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */

                /* use index information for optimized query processing - cases where NAME='Joe' type of
                   conditions appear in the query*/
                if(hindex.getVal() != 0)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Using optimized processing\n");
                    jdam.dam_describeIndex(hindex.getVal(), null, sIndexName, null, null, null);
                    iRetCode = java_optimize_exec(dam_hstmt, hindex, hset_of_condlist,iStmtType,piNumResRows);
                    jdam.dam_freeSetOfConditionList(hset_of_condlist.getVal()); /* free the set of condition list */
                    if (iRetCode != IP_SUCCESS)
                    {   /* check for errors */
                        return IP_FAILURE;
                    }
                }
                else
                {
                    /* non-optimized query processing -- cases where no WHERE clause or conditions on columns
                       otther than NAME. */
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV,"Doing a full table scan\n");
                    iRetCode = java_exec(dam_hstmt,iStmtType,piNumResRows);

                    /* check if there are any pending result rows*/
                    if(iRetCode == DAM_SUCCESS_WITH_RESULT_PENDING){
                        jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Returning with result rows pending.\n");
                        return iRetCode;
                    }

                    /* check for errors */
                    if (iRetCode != IP_SUCCESS) return iRetCode;
                }
            }
            else if (iStmtType == DAM_INSERT) {
                long         hrow;

                 /* for each row to be inserted, get the column values and insert */
                hrow = jdam.dam_getFirstInsertRow(dam_hstmt);
                while (hrow != 0) {
                    /* insert the row */
                    iRetCode = java_insert_row(dam_hstmt, hrow);
                    if (iRetCode != IP_SUCCESS) return iRetCode;

                    hrow = jdam.dam_getNextInsertRow(dam_hstmt);
                    }
                piNumResRows.setVal(m_iNumResRows);
                }
            else if (iStmtType == DAM_FETCH) {
                xo_int  piValue;

                piValue = new xo_int();

                /* check if query was cancelled */
                iRetCode = jdam.dam_getInfo(0, dam_hstmt, DAM_INFO_QUERY_CANCEL, null, piValue);

                if (piValue.getVal() > 0)
                {
                   jdam.trace(m_tmHandle, UL_TM_ERRORS, "Query is Cancelled.\n");
                   jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 0, "Query is Cancelled");
                   return DAM_FAILURE;
                }

               /* query processing */
               iRetCode = java_exec(dam_hstmt, iStmtType, piNumResRows);

               /* check if there are any pending result rows*/
               if (iRetCode == DAM_SUCCESS_WITH_RESULT_PENDING)
               {
                   jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Returning with result rows pending.\n");
                   return iRetCode;
               }

               /* check for errors*/
               if (iRetCode != IP_SUCCESS) return iRetCode;
            }
            else if (iStmtType == DAM_CLOSE) {
                   return IP_SUCCESS;
            }
            else
                return IP_FAILURE;

            if((iStmtType == DAM_SELECT || iStmtType == DAM_FETCH) && (hindex.getVal() == 0))
                jdam.trace(m_tmHandle, UL_TM_INFO, "ipExecute(): Returning with no result rows pending.\n");

            return IP_SUCCESS;
    }

    public int ipSchema(long dam_hdbc,long pMemTree,int iType, long pList, Object pSearchObj)
    {
        switch(iType)
        {
        case DAMOBJ_TYPE_CATALOG:
            {
                schemaobj_table TableObj = new schemaobj_table(OA_CATALOG_NAME,null,null,null,null,null,null,null);

                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
            }
            break;
        case DAMOBJ_TYPE_SCHEMA:
            {
                schemaobj_table TableObj = new schemaobj_table();

                TableObj.SetObjInfo(null,"SYSTEM",null,null,null,null,null,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);

                TableObj.SetObjInfo(null,OA_USER_NAME,null,null,null,null,null,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
            }
            break;
        case DAMOBJ_TYPE_TABLETYPE:
            {
                schemaobj_table TableObj = new schemaobj_table();

                TableObj.SetObjInfo(null,null,null,"SYSTEM TABLE",null,null,null,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);

                TableObj.SetObjInfo(null,null,null,"TABLE",null,null,null,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);

                TableObj.SetObjInfo(null,null,null,"VIEW",null,null,null,null);
                jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,TableObj);
            }

            break;
        case DAMOBJ_TYPE_TABLE:
            {
                schemaobj_table  pTableSearchObj = (schemaobj_table) pSearchObj;
                if (pTableSearchObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema  of table:<"+pTableSearchObj.getTableQualifier()+"."+pTableSearchObj.getTableOwner()+"."+pTableSearchObj.getTableName()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all tables is being requested\n");
                }

                if (IsMatchingTable(pTableSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE"))
                {
                    schemaobj_table TableObj = new schemaobj_table();

                    TableObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,"CURVALUE","TABLE",null,null,null,"Current Values Table");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj, TableObj);
                }
            }
            break;
        case DAMOBJ_TYPE_COLUMN:
            {
                schemaobj_column pColSearchObj = (schemaobj_column) pSearchObj;

                if (pColSearchObj != null)
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for column <"+pColSearchObj.getColumnName()+"> of table:<"+pColSearchObj.getTableQualifier()+"."+pColSearchObj.getTableOwner()+"."+pColSearchObj.getTableName()+"> is being requested\n");
                }
                else
                {
                    jdam.trace(m_tmHandle, UL_TM_MAJOR_EV, "Dynamic Schema for all columns of all tables is being requested\n");
                }

                // CURVALUE TABLE COLUMNS
                if (IsMatchingColumn(pColSearchObj, OA_CATALOG_NAME, OA_USER_NAME, "CURVALUE"))
                {
                    schemaobj_column ColumnObj = new schemaobj_column();

                    ColumnObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,"CURVALUE","NAME",(short)12,"VARCHAR",32,32,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NO_NULLS,(short)DAMOBJ_NOTSET,null,null,(short)SQL_PC_NOT_PSEUDO,(short)0,"name");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,"CURVALUE","INTVAL",(short)4,"INTEGER",4,10,(short)DAMOBJ_NOTSET,(short)0,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)SQL_PC_NOT_PSEUDO,(short)0,"Integer Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,"CURVALUE","FLOATVAL",(short)8,"DOUBLE",8,15,(short)DAMOBJ_NOTSET,(short)DAMOBJ_NOTSET,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)SQL_PC_NOT_PSEUDO,(short)0,"Float Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);

                    ColumnObj.SetObjInfo(OA_CATALOG_NAME,OA_USER_NAME,"CURVALUE","TIME",(short)11,"TIMESTAMP",16,19,(short)DAMOBJ_NOTSET,(short)0,
                        (short)XO_NULLABLE,(short)DAMOBJ_NOTSET,null,null,(short)SQL_PC_NOT_PSEUDO,(short)0,"Time Value");
                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,ColumnObj);
                }
            }
            break;

        case DAMOBJ_TYPE_STAT:
            {
                    schemaobj_stat StatObj = new schemaobj_stat(OA_CATALOG_NAME,OA_USER_NAME,"CURVALUE",(short)0,OA_CATALOG_NAME,"OA_NAME",(short)3,(short)1,"NAME",
                                                                "A",DAMOBJ_NOTSET,DAMOBJ_NOTSET,null);

                    jdam.dam_add_schemaobj(pMemTree,iType,pList,pSearchObj,StatObj);
            }
            break;

        case DAMOBJ_TYPE_FKEY:
            break;
        case DAMOBJ_TYPE_PKEY:
            break;
        case DAMOBJ_TYPE_PROC:
            break;
        case DAMOBJ_TYPE_PROC_COLUMN:
            break;
        default:
            break;
        }

       return IP_SUCCESS;

    }

    private boolean     IsMatchingTable(schemaobj_table pSearchObj,
                                           String table_qualifier,
                                           String table_owner,
                                           String table_name)
    {
        if (pSearchObj == null) return true;
        boolean  bSearchPattern = !(!bAllowSchemaSearchPattern || (jdam.dam_isSearchPatternObject(pSearchObj) == 0));

        if(!bSearchPattern)
        {
            /* match the search pattern */
            if ((pSearchObj.getTableQualifier() != null) && !pSearchObj.getTableQualifier().equalsIgnoreCase(table_qualifier))
                return false;
            if ((pSearchObj.getTableOwner() != null) && !pSearchObj.getTableOwner().equalsIgnoreCase(table_owner))
                return false;
            if ((pSearchObj.getTableName() != null) && !pSearchObj.getTableName().equalsIgnoreCase(table_name))
                return false;
        }
        else
        {
            /* match the search pattern */
            if ((pSearchObj.getTableQualifier() != null) && jdam.dam_strlikecmp(pSearchObj.getTableQualifier(),table_qualifier) != 0)
                return false;
            if ((pSearchObj.getTableOwner() != null) && jdam.dam_strlikecmp(pSearchObj.getTableOwner(),table_owner) != 0)
                return false;
            if ((pSearchObj.getTableName() != null) && jdam.dam_strlikecmp(pSearchObj.getTableName(),table_name) != 0)
                return false;
        }

        return true;
    }

    private boolean     IsMatchingColumn(schemaobj_column pSearchObj,
                                           String table_qualifier,
                                           String table_owner,
                                           String table_name)
    {
        if (pSearchObj == null) return true;
        boolean  bSearchPattern = !(!bAllowSchemaSearchPattern || (jdam.dam_isSearchPatternObject(pSearchObj) == 0));

        if(!bSearchPattern)
        {
            /* match the search pattern */
            if ((pSearchObj.getTableQualifier() != null) && !pSearchObj.getTableQualifier().equalsIgnoreCase(table_qualifier))
                return false;
            if ((pSearchObj.getTableOwner() != null) && !pSearchObj.getTableOwner().equalsIgnoreCase(table_owner))
                return false;
            if ((pSearchObj.getTableName() != null) && !pSearchObj.getTableName().equalsIgnoreCase(table_name))
                return false;
        }
        else
        {
            /* match the search pattern */
            if ((pSearchObj.getTableQualifier() != null) && jdam.dam_strlikecmp(pSearchObj.getTableQualifier(),table_qualifier) != 0)
                return false;
            if ((pSearchObj.getTableOwner() != null) && jdam.dam_strlikecmp(pSearchObj.getTableOwner(),table_owner) !=0 )
                return false;
            if ((pSearchObj.getTableName() != null) && jdam.dam_strlikecmp(pSearchObj.getTableName(),table_name) != 0)
                return false;
        }

        return true;
    }


    /* this example uses dynamic schema and only SELECT command supported, so following functions are not called */
    public int        ipDDL(long dam_hstmt, int iStmtType, xo_long piNumResRows)
    {
                return IP_FAILURE;
    }

    public int        ipProcedure(long dam_hstmt, int iType, xo_long piNumResRows)
    {
                return IP_FAILURE;
    }

    public int        ipDCL(long dam_hstmt, int iStmtType, xo_long piNumResRows)
    {
                return IP_FAILURE;
    }

    public int        ipPrivilege(int iStmtType,String pcUserName,String pcCatalog,String pcSchema,String pcObjName)
    {
                return IP_FAILURE;
    }

    public int        ipNative(long dam_hstmt, int iCommandOption, String sCommand, xo_long piNumResRows)
    {
                return IP_FAILURE;
    }

    public int        ipSchemaEx(long dam_hstmt, long pMemTree, int iType, long pList,Object pSearchObj)
    {
                return IP_FAILURE;
    }

    public int        ipProcedureDynamic(long dam_hstmt, int iType, xo_long piNumResRows)
    {
                return IP_FAILURE;
    }

    // Utility functions
    int java_optimize_exec(long dam_hstmt, xo_long hindex, xo_long hset_of_condlist,int iStmtType, xo_long piNumResRows)
        {
            long hcur_condlist;  /* DAM_HCONDLIST */
            long hcond;          /* DAM_HCOND */
            long hrow;           /* DAM_HROW */
            int iRetCode;
            m_iNumResRows = 0;
            piNumResRows.setVal(0);
            /* get the conditions on index columns */
            hcur_condlist = jdam.dam_getFirstCondList(hset_of_condlist.getVal());
            while (hcur_condlist != 0)
            {
                xo_int iLeftOp = new xo_int(0);
                xo_int iLeftValType = new xo_int(0);
                xo_int iLeftValLen = new xo_int(0);
                xo_int iStatus = new xo_int(0);
                String pLeftData;
                xo_int piMdbIndex =  new xo_int(0);
                /* Each condition list will have only one condition, since its a single column index */
                hcond = jdam.dam_getFirstCond(dam_hstmt, hcur_condlist);
                /* get details of the condition. since this example does not support BETWEEN, we do not
                   need to get the values for RIGHT part of condition */
                pLeftData = (String)jdam.dam_describeCondEx(dam_hstmt, hcond, DAM_COND_PART_LEFT, iLeftOp, iLeftValType, iLeftValLen, iStatus);
                iRetCode = iStatus.getVal();
                if (iRetCode != DAM_SUCCESS) return IP_FAILURE; /* return on error */
                /* build result rows that match current index condition */
                if (m_pMdb.FindItemByName(pLeftData,piMdbIndex))
                {   /* found a match */
                    String    sName;
                    int       iIntVal;
                    double    dDoubleVal;
                    long      lTime;
					int		  iCurRow = piMdbIndex.getVal();

                    /* read row from the memory database */
                    m_pMdb.ReadRow(iCurRow);
                    /* Get the field values from the Row */
                    sName = m_pMdb.getName(iCurRow);
                    iIntVal = m_pMdb.getIntVal(iCurRow);
                    dDoubleVal = m_pMdb.getDVal(iCurRow);
                    lTime = m_pMdb.getTime(iCurRow);
                    /* build the DAM row with the values read from memory database */
                    hrow = java_build_row(dam_hstmt, sName, iIntVal, dDoubleVal, lTime);
                    if (hrow == 0) return IP_FAILURE; /* return on error */
                    /* process the row */
                    iRetCode = java_process_row(dam_hstmt, iStmtType,hrow, iCurRow);
                    if (iRetCode != IP_SUCCESS) return iRetCode; /* return on error */
                }
                hcur_condlist = jdam.dam_getNextCondList(hset_of_condlist.getVal());
            }
            piNumResRows.setVal(m_iNumResRows);
            return IP_SUCCESS;
    }

        /* this function is called to do a full table scan */
    public int java_exec(long dam_hstmt, int iStmtType, xo_long piNumResRows)
        {
            long    hrow; /* DAM_HROW */
            int     iRetCode;
            int     iIntVal;
            double  dDoubleVal;
            long    lTime;
            boolean bFound = false;
            String  sName;

            m_iNumResRows = 0;
            piNumResRows.setVal(0);
            if(iStmtType == DAM_SELECT  || iStmtType == DAM_UPDATE || iStmtType == DAM_DELETE)
            {
                /* read row from the memory database */
                iCurrentRow.setVal(0);
                bFound = m_pMdb.FirstRow(iCurrentRow);
            }
            else
            {
                bFound = m_pMdb.NextRow(iCurrentRow);
            }
            while (bFound)
            {   /* Get the Row */
			    int iCurRow = iCurrentRow.getVal();

                sName = m_pMdb.getName(iCurRow);
                iIntVal = m_pMdb.getIntVal(iCurRow);
                dDoubleVal = m_pMdb.getDVal(iCurRow);
                lTime = m_pMdb.getTime(iCurRow);
                /* build the DAM row with the values read from memory database */
                hrow = java_build_row(dam_hstmt, sName, iIntVal, dDoubleVal, lTime);
                if (hrow == 0) return IP_FAILURE; /* return on error */
                /* process the row */
                iRetCode = java_process_row(dam_hstmt, iStmtType,hrow, iCurRow);
                if (iRetCode != IP_SUCCESS) return iRetCode; /* return on error */

                piNumResRows.setVal(m_iNumResRows);
                if(iStmtType == DAM_SELECT  || iStmtType == DAM_FETCH)
                {
                      if(m_iNumResRows == iFetchSize)
                            return DAM_SUCCESS_WITH_RESULT_PENDING;
                }
                /* read next row from the memory database */
                bFound = m_pMdb.NextRow(iCurrentRow);
            }

            return IP_SUCCESS;
    }
        /* given the raw values, build a DAM ROW structure */
    public long java_build_row(long dam_hstmt, String sName, int iIntVal, double dDoubleVal, long lTime)
        {
            long    hrow = 0;
            int     iRetCode;
            xo_tm   xoTime;
            /* allocate a new row */
            hrow = jdam.dam_allocRow(dam_hstmt);
            /* change the column values to data types accepted by DAM*/
            xoTime = java_cvrt_time_to_xotm(lTime);
            if(xoTime == null)
                return 0;
            /* set values for columns of the row */
            iRetCode = jdam.dam_addCharValToRow(dam_hstmt, hrow, hcolName,sName,XO_NTS);
            if (iRetCode != DAM_SUCCESS) return 0;
            iRetCode = jdam.dam_addIntValToRow(dam_hstmt, hrow, hcolIntVal, iIntVal,0);
            if (iRetCode != DAM_SUCCESS) return 0;
            iRetCode = jdam.dam_addDoubleValToRow(dam_hstmt, hrow, hcolDoubleVal,dDoubleVal,0);
            if (iRetCode != DAM_SUCCESS) return 0;
            iRetCode = jdam.dam_addTimeStampValToRow(dam_hstmt, hrow, hcolTime,xoTime,0);
            if (iRetCode != DAM_SUCCESS) return 0;
            return hrow;
        }

    int java_process_row(long dam_hstmt, int iStmtType,long hrow, int rowIndex)
        {
            switch (iStmtType)
            {
                case DAM_SELECT:
                case DAM_FETCH:
                    if (jdam.dam_isTargetRow(dam_hstmt, hrow) == DAM_TRUE){
                        jdam.dam_addRowToTable(dam_hstmt, hrow);
                        m_iNumResRows++;
                    }
                    else
                        jdam.dam_freeRow(hrow);
                    break;
                case DAM_UPDATE:
                    java_update_row(dam_hstmt, hrow, rowIndex);
                    break;

                case DAM_DELETE:
                    if (jdam.dam_isTargetRow(dam_hstmt, hrow) == DAM_TRUE) {
                        jdam.trace(m_tmHandle, UL_TM_INFO,"Delete Row:" + rowIndex + "\n");
                        m_pMdb.DeleteRow(rowIndex);
                        m_iNumResRows++;
                        }
                    jdam.dam_freeRow(hrow);
                    break;
                default:
                    break;
            }
            return IP_SUCCESS;
        }

    int java_update_row(long dam_hstmt, long hrow, int rowIndex)
        {
        long     hInputRow;
        long     hRowElem;
        long            hcol;
        StringBuffer    sColName;
        xo_int          iValueStatus;
        String          sName;
        Integer         intVal;
        Double          doubleVal;
        xo_tm           timeVal;


        hInputRow = jdam.dam_getUpdateRow(dam_hstmt, hrow);

        m_pMdb.SetUpdateRow(rowIndex);

        hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hInputRow);
        sColName = new StringBuffer();
        iValueStatus = new xo_int(1);

        while (hRowElem != 0) {

            sColName.delete(0, sColName.length());
            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, sColName, null, null);
            if (sColName.toString().compareToIgnoreCase("NAME") == 0) {
                sName = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
                m_pMdb.setName(sName,rowIndex);
                }
            else if (sColName.toString().compareToIgnoreCase("INTVAL") == 0) {
                intVal = (Integer) jdam.dam_getValueToSet(hRowElem, XO_TYPE_INTEGER, iValueStatus);
                if (intVal != null)
                    m_pMdb.setIntVal(intVal.intValue(),rowIndex);
                else
                    m_pMdb.setIntVal(0,rowIndex);
                }
            else if (sColName.toString().compareToIgnoreCase("FLOATVAL") == 0) {
                doubleVal = (Double) jdam.dam_getValueToSet(hRowElem, XO_TYPE_DOUBLE, iValueStatus);
                if (doubleVal != null)
                    m_pMdb.setDVal(doubleVal.doubleValue(),rowIndex);
                else
                    m_pMdb.setDVal(0,rowIndex);
                }
            else if (sColName.toString().compareToIgnoreCase("TIME") == 0) {
                timeVal = (xo_tm) jdam.dam_getValueToSet(hRowElem, XO_TYPE_TIMESTAMP, iValueStatus);
                if (timeVal != null)
                    m_pMdb.setTime(java_cvrt_time_to_xotm(timeVal),rowIndex);
                else
                    m_pMdb.setTime(0,rowIndex);
                }
            hRowElem = jdam.dam_getNextValueSet(dam_hstmt);
            }

        m_pMdb.UpdateRow(rowIndex);
        m_iNumResRows++;

        return IP_SUCCESS;
        }

    int java_insert_row(long dam_hstmt, long hrow)
        {
        long            hRowElem;
        long            hcol;
        StringBuffer    sColName;
        xo_int          iValueStatus;
        String          sName;
        Integer         intVal;
        Double          doubleVal;
        xo_tm           timeVal;
		xo_int	        piIndex = new xo_int(0);


        if (!m_pMdb.InsertRow(piIndex)) {
            jdam.dam_addError(0, dam_hstmt, DAM_IP_ERROR, 1, "New records cannot be inserted");
            return IP_FAILURE;
        }

        int iCurRow = piIndex.getVal();

        hRowElem = jdam.dam_getFirstValueSet(dam_hstmt, hrow);
        sColName = new StringBuffer();
        iValueStatus = new xo_int(1);

        while (hRowElem != 0) {

            sColName.delete(0, sColName.length());
            hcol = jdam.dam_getColToSet(hRowElem);
            jdam.dam_describeCol(hcol, null, sColName, null, null);
            if (sColName.toString().compareToIgnoreCase("NAME") == 0) {
                sName = (String) jdam.dam_getValueToSet(hRowElem, XO_TYPE_CHAR, iValueStatus);
                m_pMdb.setName(sName,iCurRow);
                }
            else if (sColName.toString().compareToIgnoreCase("INTVAL") == 0) {
                intVal = (Integer) jdam.dam_getValueToSet(hRowElem, XO_TYPE_INTEGER, iValueStatus);
                if (intVal != null)
                    m_pMdb.setIntVal(intVal.intValue(),iCurRow);
                else
                    m_pMdb.setIntVal(0,iCurRow);
                }
            else if (sColName.toString().compareToIgnoreCase("FLOATVAL") == 0) {
                doubleVal = (Double) jdam.dam_getValueToSet(hRowElem, XO_TYPE_DOUBLE, iValueStatus);
                if (doubleVal != null)
                    m_pMdb.setDVal(doubleVal.doubleValue(),iCurRow);
                else
                    m_pMdb.setDVal(0,iCurRow);
                }
            else if (sColName.toString().compareToIgnoreCase("TIME") == 0) {
                timeVal = (xo_tm) jdam.dam_getValueToSet(hRowElem, XO_TYPE_TIMESTAMP, iValueStatus);
                if (timeVal != null)
                    m_pMdb.setTime(java_cvrt_time_to_xotm(timeVal),iCurRow);
                else
                    m_pMdb.setTime(0,iCurRow);
                }
            hRowElem = jdam.dam_getNextValueSet(dam_hstmt);
            }

        m_iNumResRows++;

        return IP_SUCCESS;
        }

    xo_tm java_cvrt_time_to_xotm(long lTime)
        {
            Date       dt  =  new Date(lTime * 1000);
            Calendar   pTm = Calendar.getInstance();
            xo_tm  pXoTm = new xo_tm();

            pTm.setTime(dt);

            /* copy the local time to xo_tm variable */
            pXoTm.setVal(xo_tm.SECOND,pTm.get(Calendar.SECOND));
            pXoTm.setVal(xo_tm.MINUTE,pTm.get(Calendar.MINUTE));
            pXoTm.setVal(xo_tm.HOUR,pTm.get(Calendar.HOUR_OF_DAY));
            pXoTm.setVal(xo_tm.DAY_OF_MONTH,pTm.get(Calendar.DAY_OF_MONTH));
            pXoTm.setVal(xo_tm.MONTH,pTm.get(Calendar.MONTH));
            pXoTm.setVal(xo_tm.YEAR,pTm.get(Calendar.YEAR));
            pXoTm.setVal(xo_tm.DAY_OF_WEEK,pTm.get(Calendar.DAY_OF_WEEK));
            pXoTm.setVal(xo_tm.DAY_OF_YEAR,pTm.get(Calendar.DAY_OF_YEAR));

            if(pTm.isSet(Calendar.DST_OFFSET))
                pXoTm.setVal(xo_tm.IS_DST,1);
            else
                pXoTm.setVal(xo_tm.IS_DST,0);

            pXoTm.setVal(xo_tm.FRACTION,pTm.get(Calendar.MILLISECOND) * 1000000);
            return pXoTm;
    }

    public long java_cvrt_time_to_xotm(xo_tm pXoTm)
        {
        Date        dt;
        Calendar   pTm = Calendar.getInstance();

        pTm.set(Calendar.SECOND, pXoTm.getVal(xo_tm.SECOND));
        pTm.set(Calendar.MINUTE, pXoTm.getVal(xo_tm.MINUTE));
        pTm.set(Calendar.HOUR_OF_DAY, pXoTm.getVal(xo_tm.HOUR));
        pTm.set(Calendar.DAY_OF_MONTH, pXoTm.getVal(xo_tm.DAY_OF_MONTH));
        pTm.set(Calendar.MONTH, pXoTm.getVal(xo_tm.MONTH));
        pTm.set(Calendar.YEAR, pXoTm.getVal(xo_tm.YEAR));
        /*
        pTm.set(Calendar.DAY_OF_WEEK, pXoTm.getVal(xo_tm.DAY_OF_WEEK));
        pTm.set(Calendar.DAY_OF_YEAR, pXoTm.getVal(xo_tm.DAY_OF_YEAR));
        */

        dt = pTm.getTime();
        return dt.getTime()/1000;
        }

    public scalar_function[] ipGetScalarFunctions()
    {
        scalar_function[] MyFuncs = new scalar_function[4];

        MyFuncs[0] = new scalar_function("INTVAL",1,"ip_func_intval",XO_TYPE_INTEGER,1);
        MyFuncs[1] = new scalar_function("DOUBLEVAL",1,"ip_func_doubleval",XO_TYPE_DOUBLE,1);
        MyFuncs[2] = new scalar_function("CHARVAL",1,"ip_func_charval",XO_TYPE_CHAR,200,200,0,1);
	MyFuncs[3] = new scalar_function("IPNAME",1,"ip_func_ipname",XO_TYPE_CHAR,25,25,0,-3);

        return MyFuncs;
    }


    public long ip_func_intval(long hstmt,long pMemTree,long hValExpList)
    {
        long hVal;
        long hValExp;
        xo_int piRetCode = new xo_int(0);
        Integer iObj;

        /* get the input */
        hValExp = jdam.dam_getFirstValExp(hValExpList);
        iObj = (Integer) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_INTEGER, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(iObj == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, null, XO_NULL_DATA);
            return hVal;
        }

        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_INTEGER, iObj, 0);

        return hVal;
    }

    public long ip_func_doubleval(long hstmt,long pMemTree,long hValExpList)
    {
        long hVal;
        long hValExp;
        xo_int piRetCode = new xo_int(0);
        Double dObj;

        /* get the input */
        hValExp = jdam.dam_getFirstValExp(hValExpList);
        dObj = (Double) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_DOUBLE, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(dObj == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, null, XO_NULL_DATA);
            return hVal;
        }

        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_DOUBLE, dObj, 0);

        return hVal;
    }

    public long ip_func_charval(long hstmt,long pMemTree,long hValExpList)
    {
        long hVal;
        long hValExp;
        xo_int piRetCode = new xo_int(0);
        String strObj;

        /* get the input */
        hValExp = jdam.dam_getFirstValExp(hValExpList);
        strObj = (String) jdam.dam_getValueOfExp(pMemTree, hValExpList, hValExp, XO_TYPE_CHAR, piRetCode);
        if(piRetCode.getVal() != DAM_SUCCESS)
            return 0;
        if(strObj == null)
        {
            hVal = jdam.dam_createVal(pMemTree, XO_TYPE_CHAR, null, XO_NULL_DATA);
            return hVal;
        }

        hVal = jdam.dam_createVal(pMemTree, XO_TYPE_CHAR, strObj, strObj.length());

        return hVal;
    }

    public long ip_func_ipname(long hstmt,long pMemTree,long hValExpList)
    {
    	long hVal;
    	hVal=jdam.dam_createVal(pMemTree, XO_TYPE_CHAR,"Java Module, example3 IP", XO_NTS);
    	return hVal;
    }

}
