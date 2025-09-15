package oajava.excel;

public final class ExcelInitException extends Exception {
    public final int code;
    public ExcelInitException(int code, String message) {
        super(message);
        this.code = code;
    }
}
