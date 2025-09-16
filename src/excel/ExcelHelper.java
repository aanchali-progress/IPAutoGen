package oajava.excel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import oajava.sql.jdam;
import static oajava.sql.ip.*;

/**
 * Excel I/O helper (Apache POI) — .xlsx only.
 * - Create: new XSSFWorkbook(), save immediately
 * - Open:   WorkbookFactory.create(file)
 * - If malformed: backup original and re-init a fresh .xlsx
 * - Preflight check prints exactly which dependency class is missing
 */
public final class ExcelHelper {

    private final String excelPath;
    private final long   tmHandle;

    private Workbook workbook;
    private File     file;
    
 // at class level
    private volatile boolean dirty = false;

    private void markDirty() { dirty = true; }
    public boolean isDirty() { return dirty; }
    private void clearDirty() { dirty = false; }


    public ExcelHelper(String excelPath, long tmHandle) throws ExcelInitException {
        this.excelPath = excelPath;
        this.tmHandle  = tmHandle;
        this.file      = new File(excelPath);

        // A bit less strict on zip bomb (optional)
        try { ZipSecureFile.setMinInflateRatio(0.001); } catch (Throwable ignore) {}

        // Ensure parent directory exists
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                jdam.trace(tmHandle, UL_TM_INFO, "Parent dir creation failed or already exists: " + parent + "\n");
            }
        }

        // ---- Preflight: verify POI runtime deps are present ----
        String missing = dependencyPreflight();
        if (!missing.isEmpty()) {
            String cp = System.getProperty("java.class.path", "");
            throw new ExcelInitException(26,
                "Missing runtime classes:\n" + missing +
                "\nAdd the required jars (poi, poi-ooxml, poi-ooxml-full OR ooxml-schemas, xmlbeans, " +
                "commons-io, commons-compress, commons-collections4, commons-codec, log4j-api).\n" +
                "Classpath:\n" + cp);
        }

        // (A) If missing or 0 bytes, create a valid empty .xlsx immediately
        if (!file.exists() || file.length() == 0L) {
            jdam.trace(tmHandle, UL_TM_INFO,
                (file.exists() ? "Blank (0B) file detected, " : "No file found, ") +
                "creating new workbook: " + excelPath + "\n");
            try {
                this.workbook = new XSSFWorkbook();           // <-- same as your smoke test
            } catch (Exception e) {
                String cp = System.getProperty("java.class.path", "");
                throw new ExcelInitException(27,
                    "Failed at new XSSFWorkbook(): " + e.getClass().getSimpleName() +
                    " -> " + safeMsg(e) + "\nClasspath:\n" + cp);
            }
            try {
				saveWorkbook();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // persist immediately so later reloads work
            return;
        }

        // (B) Try to open existing workbook; on format issues, back up and re-init a new workbook
        try {
            this.workbook = WorkbookFactory.create(file);     // <-- same as your smoke test
            jdam.trace(tmHandle, UL_TM_INFO, "Opened existing workbook: " + excelPath + "\n");
        } catch (IllegalArgumentException iae) {
            backupAndReinit("Failed to parse as XLSX: " + iae.getMessage());
        } catch (EncryptedDocumentException ede) {
            throw new ExcelInitException(22,
                "Excel file is password-protected: " + excelPath + ". Decrypt it before use.");
        } catch (IOException ioe) {
            throw new ExcelInitException(24,
                "Cannot open Excel file: " + excelPath + " (" + ioe.getClass().getSimpleName() + ": " + ioe.getMessage() + "). " +
                "Check file locks/permissions.");
        } catch (Exception e) {
            String cp = System.getProperty("java.class.path", "");
            throw new ExcelInitException(28,
                "Failed while opening workbook: " + e.getClass().getSimpleName() +
                " -> " + safeMsg(e) + "\nClasspath:\n" + cp);
        }
    }

    
    // --------------------------- Sheet & row ops -----------------------------

    public synchronized boolean createSheet(String sheetName, java.util.List<String> headers) {
        if (workbook.getSheet(sheetName) != null) return false;
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i, CellType.STRING);
            cell.setCellValue(headers.get(i));
            sheet.autoSizeColumn(i);
        }
        markDirty();
        return true;
    }

 // CHANGED
    public boolean deleteSheet(String sheetName) {
        int idx = workbook.getSheetIndex(sheetName);
        if (idx < 0) return false;

        // If this is the only sheet, refuse to drop so we don't create a zero-sheet workbook.
        if (workbook.getNumberOfSheets() == 1) {
            // leave the workbook intact; caller will report a drop failure
            return false;
        }

        workbook.removeSheetAt(idx);
        markDirty();
        return true;
    }

 // NEW helper
    private static int findAppendRowIndex(org.apache.poi.ss.usermodel.Sheet sh) {
        int last = sh.getLastRowNum();
        // walk back over any null rows at the end
        while (last >= 0 && sh.getRow(last) == null) last--;
        return Math.max(0, last + 1);
    }

    
    public synchronized int getRowCount(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return 0;
        int last = sheet.getLastRowNum();
        int lastNonEmpty = 0;
        for (int r = last; r >= 1; r--) {
            Row row = sheet.getRow(r);
            if (!isRowEmpty(row)) { lastNonEmpty = r; break; }
        }
        return lastNonEmpty; // data rows only (header at 0)
    }

    public synchronized RowData readRow(String sheetName, int dataRowIndex) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return null;
        Row header = sheet.getRow(0);
        int physRow = dataRowIndex + 1;
        Row row = sheet.getRow(physRow);
        if (row == null || isRowEmpty(row)) return null;

        int colCount = (header != null && header.getLastCellNum() > 0)
                     ? header.getLastCellNum() : row.getLastCellNum();
        RowData rd = new RowData(colCount);
        for (int c = 0; c < colCount; c++) {
            Cell cell = row.getCell(c);
            rd.setValue(c, readCell(cell));
        }
        return rd;
    }

 // CHANGED
    public int addEmptyRow(String sheetName) {
        org.apache.poi.ss.usermodel.Sheet sh = workbook.getSheet(sheetName);
        if (sh == null) return -1;

        // Next logical index equals current data row count (excludes header at 0)
        int logicalIdx = getRowCount(sheetName);
        int physRow = logicalIdx + 1; // map logical -> physical

        org.apache.poi.ss.usermodel.Row row = sh.getRow(physRow);
        if (row == null) row = sh.createRow(physRow);

        markDirty();
        return logicalIdx; // return logical index, expected by caller
    }

    public synchronized void setCell(String sheetName, int dataRowIndex, ColumnMeta colMeta, Object value) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return;
        int physRow = dataRowIndex + 1;
        Row row = sheet.getRow(physRow);
        if (row == null) row = sheet.createRow(physRow);

        int colIndex = getColumnIndexByName(sheet, colMeta.name);
        if (colIndex < 0) colIndex = ensureHeaderColumn(sheet, colMeta.name);

        Cell cell = row.getCell(colIndex);
        if (cell == null) cell = row.createCell(colIndex);

        if (value == null) { cell.setBlank(); return; }

        String t = (colMeta.sqlType == null) ? "VARCHAR" : colMeta.sqlType.toUpperCase(Locale.ROOT);
        switch (t) {
            case "INTEGER":
            case "SMALLINT":
            case "TINYINT":
            case "BIGINT":
            case "DOUBLE":
            case "FLOAT":
            case "REAL":
            case "DECIMAL":
            case "NUMERIC": {
                double dv = (value instanceof Number) ? ((Number)value).doubleValue() : parseDoubleSafe(value.toString());
                cell.setCellValue(dv);
                break;
            }
            case "BIT": {
                boolean bv = (value instanceof Boolean) ? (Boolean)value : parseBooleanSafe(value.toString());
                cell.setCellValue(bv);
                break;
            }
            case "DATE":
            case "TIME":
            case "TIMESTAMP": {
                Date d = coerceToDate(value);
                if (d != null) {
                    cell.setCellValue(d);
                    CellStyle style = workbook.createCellStyle();
                    DataFormat df = workbook.createDataFormat();
                    style.setDataFormat(df.getFormat("yyyy-mm-dd hh:mm:ss"));
                    cell.setCellStyle(style);
                } else {
                    cell.setBlank();
                }
                break;
            }
            default:
                cell.setCellValue(String.valueOf(value));
        }
        markDirty();
    }

    public synchronized void deleteRow(String sheetName, int dataRowIndex) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return;
        int physRow = dataRowIndex + 1;
        int lastRow = sheet.getLastRowNum();
        if (physRow >= 1 && physRow <= lastRow) {
            sheet.removeRow(sheet.getRow(physRow));
            if (physRow < lastRow) sheet.shiftRows(physRow + 1, lastRow, -1, true, true);
        }
        markDirty();
    }

 // CHANGED
    public synchronized void saveWorkbook() throws IOException {
        if (workbook == null) return;

        final java.nio.file.Path target = file.toPath();
        final java.nio.file.Path dir    = target.getParent();
        final java.nio.file.Path tmp    = java.nio.file.Files.createTempFile(
                dir, target.getFileName().toString(), ".tmp");

        boolean moved = false;
        try (java.io.OutputStream out = java.nio.file.Files.newOutputStream(
                tmp,
                java.nio.file.StandardOpenOption.WRITE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {
            workbook.write(out);   // if this throws, original is untouched
            out.flush();

            try {
                java.nio.file.Files.move(
                    tmp, target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE
                );
                moved = true;
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                java.nio.file.Files.move(tmp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                moved = true;
            }
        } finally {
            // If move failed or anything threw after createTempFile, clean it up.
            if (!moved) {
                try { java.nio.file.Files.deleteIfExists(tmp); } catch (Exception ignore) {}
            }
        }
        clearDirty();
    }



    public synchronized void reloadWorkbook() {
        try {
            if (workbook != null) workbook.close();
            this.workbook = WorkbookFactory.create(file);
        } catch (Exception ioe) {
            jdam.trace(tmHandle, UL_TM_ERRORS, "Failed to reload workbook: " + ioe + "\n");
        }
    }

    public synchronized void closeWorkbook() {
        try { if (workbook != null) workbook.close(); } catch (IOException ignore) {}
    }

    // --------------------------- helpers ------------------------------------

    /** Probe for common classes POI needs; return a multi-line string of missing ones (or empty if all good). */
    private String dependencyPreflight() {
        String[] probe = new String[] {
            // POI core
            "org.apache.poi.xssf.usermodel.XSSFWorkbook",
            "org.apache.poi.ss.usermodel.Workbook",
            // OOXML schemas (from poi-ooxml-full OR ooxml-schemas)
            "org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook",
            // XMLBeans
            "org.apache.xmlbeans.XmlObject",
            // Commons deps
            "org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream",
            "org.apache.commons.compress.archivers.zip.ZipFile",
            "org.apache.commons.collections4.ListValuedMap",
            "org.apache.commons.codec.binary.Base64",
            // Logging API used by POI
            "org.apache.logging.log4j.Logger"
        };
        StringBuilder missing = new StringBuilder();
        for (String cn : probe) {
            try { Class.forName(cn, false, getClass().getClassLoader()); }
            catch (Throwable t) { missing.append(" - ").append(cn).append(" (").append(t.getClass().getSimpleName()).append(")\n"); }
        }
        return missing.toString();
    }

    private void backupAndReinit(String reason) throws ExcelInitException {
        jdam.trace(tmHandle, UL_TM_INFO, "Workbook open failed (" + reason + "). Backing up and reinitializing.\n");
        File backup = new File(file.getParentFile(),
                file.getName() + ".bak-" + System.currentTimeMillis());
        try {
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            jdam.trace(tmHandle, UL_TM_ERRORS, "Backup copy failed: " + ioe + "\n");
        }
        try {
            this.workbook = new XSSFWorkbook();
        } catch (Throwable e) {
            String cp = System.getProperty("java.class.path", "");
            throw new ExcelInitException(27,
                "Failed at new XSSFWorkbook() during reinit: " + e.getClass().getSimpleName() +
                " -> " + safeMsg(e) + "\nClasspath:\n" + cp);
        }
        try {
			saveWorkbook();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // overwrite the bad file with a valid empty .xlsx
    }

    private static String safeMsg(Throwable t) {
        String m = t.getMessage();
        return (m == null) ? "(no message)" : m;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        short lastCell = row.getLastCellNum();
        if (lastCell < 0) return true;
        for (int c = 0; c < lastCell; c++) {
            Cell cell = row.getCell(c);
            if (cell == null) continue;
            if (cell.getCellType() == CellType.BLANK) continue;
            if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty()) continue;
            return false;
        }
        return true;
    }

    private Object readCell(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case BLANK:   return null;
            case BOOLEAN: return cell.getBooleanCellValue();
            case STRING:  return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) return cell.getDateCellValue();
                return cell.getNumericCellValue();
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case BOOLEAN: return cell.getBooleanCellValue();
                    case STRING:  return cell.getStringCellValue();
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) return cell.getDateCellValue();
                        return cell.getNumericCellValue();
                    default: return null;
                }
            default: return null;
        }
    }

    private int getColumnIndexByName(Sheet sheet, String name) {
        Row header = sheet.getRow(0);
        if (header == null) return -1;
        short last = header.getLastCellNum();
        for (int i = 0; i < last; i++) {
            Cell cell = header.getCell(i);
            if (cell != null && name.equalsIgnoreCase(cell.getStringCellValue())) return i;
        }
        return -1;
    }

    private int ensureHeaderColumn(Sheet sheet, String name) {
        Row header = sheet.getRow(0);
        if (header == null) header = sheet.createRow(0);
        int idx = header.getLastCellNum();
        if (idx < 0) idx = 0;
        Cell cell = header.createCell(idx, CellType.STRING);
        cell.setCellValue(name);
        sheet.autoSizeColumn(idx);
        return idx;
    }

    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }
    private static boolean parseBooleanSafe(String s) {
        String t = s.trim().toLowerCase(Locale.ROOT);
        return "true".equals(t) || "1".equals(t) || "yes".equals(t);
    }

    // No javax.xml.bind.DatatypeConverter (works on Java 11+)
    private static Date coerceToDate(Object v) {
        if (v == null) return null;
        if (v instanceof Date) return (Date) v;
        if (v instanceof Number) return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(((Number) v).doubleValue());
        if (v instanceof CharSequence) {
            String s = v.toString().trim();
            // Try ISO-8601
            try { return Date.from(Instant.parse(s)); } catch (DateTimeParseException ignored) {}
            try { return Date.from(OffsetDateTime.parse(s).toInstant()); } catch (DateTimeParseException ignored) {}
            try {
                LocalDateTime ldt = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH[:mm[:ss[.SSS]]]]"));
                return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {}
            // Excel-style numeric-in-string?
            try { return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble(s)); } catch (Exception ignored) {}
        }
        return null;
    }
}
