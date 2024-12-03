package com.datacompare.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class DataSet {
    private List<DataRow> rows = new ArrayList<>();
    private List<String> fields = new ArrayList<>();
    
    public void addRow(DataRow row) {
        rows.add(row);
    }
    
    public Map<String, DataRow> groupByKey(String primaryKey) {
        return rows.stream()
            .collect(Collectors.toMap(
                row -> row.getKeyValue(primaryKey),
                row -> row,
                (existing, replacement) -> existing
            ));
    }
} 