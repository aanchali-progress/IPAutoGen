package oajava.excel;

public final class ColumnMeta {
    public final String  name;
    public final String  sqlType;   // e.g., "INTEGER", "VARCHAR", "DATE"...
    public final int     precision;
    public final int     scale;
    public final boolean isNullable;

    public ColumnMeta(String name, String sqlType, int precision, int scale, boolean isNullable) {
        this.name = name;
        this.sqlType = (sqlType == null) ? "VARCHAR" : sqlType.toUpperCase();
        this.precision = precision;
        this.scale = scale;
        this.isNullable = isNullable;
    }
}
