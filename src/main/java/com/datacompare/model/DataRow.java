package com.datacompare.model;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class DataRow {
    private Map<String, Object> data = new HashMap<>();
    
    public void setValue(String field, Object value) {
        data.put(field, value);
    }
    
    public Object getValue(String field) {
        return data.get(field);
    }
    
    public String getKeyValue(String primaryKey) {
        Object value = data.get(primaryKey);
        return value != null ? value.toString() : null;
    }
} 