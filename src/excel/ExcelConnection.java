package oajava.excel;

/**
 * Per-connection context. Constructor performs all heavy init.
 */
public final class ExcelConnection {
    public final String      excelFilePath;
    public final ExcelHelper excelHelper;
    public final ExcelSchema schema;

    public ExcelConnection(String excelFilePath, long tmHandle) throws ExcelInitException {
        if (excelFilePath == null || excelFilePath.isEmpty()) {
            throw new ExcelInitException(1, "ExcelFile property not provided");
        }
        this.excelFilePath = excelFilePath;

        // Opens existing or creates new workbook
        this.excelHelper = new ExcelHelper(excelFilePath, tmHandle);

        // Loads schema or initializes empty
        this.schema = new ExcelSchema(excelFilePath + ".schema", tmHandle);
    }

    public void save()   { excelHelper.saveWorkbook(); schema.save(); }
    public void reload() { excelHelper.reloadWorkbook(); schema.reload(); }
    public void close()  { excelHelper.closeWorkbook(); }
}
