package oajava.excel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import oajava.sql.jdam;
import static oajava.sql.ip.*;   // brings in XO_TYPE_* and SQL_PC_NOT_PSEUDO etc.

/**
 * Simple schema persistence for tables/columns.
 * File format (UTF-8):
 *   # ExcelSchema v1
 *   [TABLE] tableName
 *   COLUMN|name|type|precision|scale|nullable
 *   ...
 *
 * NOTE: Type mapping now uses XO_TYPE_* (OpenAccess host variable types),
 * not SQL_* constants.
 */
public final class ExcelSchema {

    private final String schemaPath;
    private final long   tmHandle;

    private final List<TableMeta> tables = new ArrayList<>();

    public ExcelSchema(String schemaPath, long tmHandle) {
        this.schemaPath = schemaPath;
        this.tmHandle   = tmHandle;
        File f = new File(schemaPath);
        if (f.exists()) {
            if (!load()) initEmpty();
        } else {
            initEmpty();
            save();
        }
    }

    public void initEmpty() {
        tables.clear();
    }

    public List<TableMeta> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public boolean hasTable(String name) {
        return getTable(name) != null;
    }

    public TableMeta getTable(String name) {
        for (TableMeta t : tables) {
            if (t.name.equalsIgnoreCase(name)) return t;
        }
        return null;
    }

    public void addTable(TableMeta t) {
        if (!hasTable(t.name)) tables.add(t);
    }

    public void removeTable(String name) {
        tables.removeIf(t -> t.name.equalsIgnoreCase(name));
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(schemaPath), StandardCharsets.UTF_8))) {
            pw.println("# ExcelSchema v1");
            for (TableMeta t : tables) {
                pw.println("[TABLE] " + t.name);
                for (ColumnMeta c : t.columns) {
                    pw.println("COLUMN|" + escape(c.name) + "|" + escape(c.sqlType) + "|" +
                               c.precision + "|" + c.scale + "|" + (c.isNullable ? 1 : 0));
                }
            }
        } catch (IOException ioe) {
            jdam.trace(tmHandle, UL_TM_ERRORS, "Failed to save schema: " + ioe + "\n");
        }
    }

    public void reload() {
        if (!load()) {
            jdam.trace(tmHandle, UL_TM_INFO, "Schema reload failed; initializing empty\n");
            initEmpty();
        }
    }

    private boolean load() {
        tables.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(schemaPath), StandardCharsets.UTF_8))) {
            String line;
            TableMeta current = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("[TABLE]")) {
                    String name = line.substring(7).trim();
                    current = new TableMeta(name);
                    tables.add(current);
                } else if (line.startsWith("COLUMN|") && current != null) {
                    String[] parts = line.split("\\|", -1);
                    // COLUMN|name|type|precision|scale|nullable
                    String name = unescape(parts[1]);
                    String type = unescape(parts[2]).toUpperCase(Locale.ROOT);
                    int precision = parseIntSafe(parts[3], 0);
                    int scale     = parseIntSafe(parts[4], 0);
                    boolean nullable = "1".equals(parts[5]);
                    current.columns.add(new ColumnMeta(name, type, precision, scale, nullable));
                }
            }
            return true;
        } catch (IOException ioe) {
            jdam.trace(tmHandle, UL_TM_ERRORS, "Failed to load schema: " + ioe + "\n");
            return false;
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("|", "\\p");
    }
    private static String unescape(String s) {
        return s.replace("\\p", "|").replace("\\\\", "\\");
    }
    private static int parseIntSafe(String s, int d) { try { return Integer.parseInt(s); } catch (Exception e) { return d; } }

    // ===================== XO-TYPE MAPPING =====================

    /**
     * Map a SQL type name (as stored in ColumnMeta.sqlType) to an XO_TYPE_* code.
     */
    public short mapTypeNameToCode(String typeName) {
        if (typeName == null) return XO_TYPE_CHAR;
        switch (typeName.toUpperCase(Locale.ROOT)) {
            case "INTEGER":   return XO_TYPE_INTEGER;
            case "SMALLINT":  return XO_TYPE_SMALLINT;
            case "TINYINT":   return XO_TYPE_TINYINT;
            case "BIGINT":    return XO_TYPE_BIGINT;

            case "DOUBLE":    return XO_TYPE_DOUBLE;
            case "FLOAT":     return XO_TYPE_DOUBLE; // OA commonly treats FLOAT as DOUBLE
            case "REAL":      return XO_TYPE_REAL;

            case "NUMERIC":
            case "DECIMAL":   return XO_TYPE_DECIMAL;

            case "CHAR":
            case "VARCHAR":
            case "LONGVARCHAR": return XO_TYPE_CHAR;

            case "WCHAR":
            case "WVARCHAR":
            case "WLONGVARCHAR": return XO_TYPE_WCHAR;

            case "DATE":      return XO_TYPE_DATE;
            case "TIME":      return XO_TYPE_TIME;
            case "TIMESTAMP": return XO_TYPE_TIMESTAMP;

            case "BIT":       return XO_TYPE_BIT;

            default:          return XO_TYPE_CHAR;
        }
    }

    /**
     * Map an XO_TYPE_* code back to a canonical SQL type name string we store in schema.
     */
    public String mapTypeCodeToName(short xoType) {
        switch (xoType) {
            case XO_TYPE_INTEGER:   return "INTEGER";
            case XO_TYPE_SMALLINT:  return "SMALLINT";
            case XO_TYPE_TINYINT:   return "TINYINT";
            case XO_TYPE_BIGINT:    return "BIGINT";

            case XO_TYPE_DOUBLE:    return "DOUBLE";
            case XO_TYPE_REAL:      return "REAL";
            case XO_TYPE_DECIMAL:   return "DECIMAL";

            case XO_TYPE_CHAR:      return "VARCHAR";
            case XO_TYPE_WCHAR:     return "WVARCHAR";

            case XO_TYPE_DATE:      return "DATE";
            case XO_TYPE_TIME:      return "TIME";
            case XO_TYPE_TIMESTAMP: return "TIMESTAMP";

            case XO_TYPE_BIT:       return "BIT";

            default:                return "VARCHAR";
        }
    }
}
