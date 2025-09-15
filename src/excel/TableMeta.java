package oajava.excel;

import java.util.*;

public final class TableMeta {
    public final String name;
    public final List<ColumnMeta> columns = new ArrayList<>();

    public TableMeta(String name) {
        this.name = name;
    }

    public ColumnMeta getColumn(String columnName) {
        for (ColumnMeta c : columns) {
            if (c.name.equalsIgnoreCase(columnName)) return c;
        }
        return null;
    }

    public List<String> getColumnNames() {
        List<String> names = new ArrayList<>(columns.size());
        for (ColumnMeta c : columns) names.add(c.name);
        return names;
    }
}
