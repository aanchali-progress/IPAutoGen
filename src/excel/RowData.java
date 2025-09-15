package oajava.excel;

import java.util.ArrayList;
import java.util.List;

public final class RowData {
    private final List<Object> values;

    public RowData(int columnCount) {
        values = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) values.add(null);
    }

    public Object getValue(int index) {
        return (index >= 0 && index < values.size()) ? values.get(index) : null;
    }

    public void setValue(int index, Object value) {
        if (index >= 0) {
            while (index >= values.size()) values.add(null);
            values.set(index, value);
        }
    }

    public int size() { return values.size(); }
}
