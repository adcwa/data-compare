package com.datacompare.query;

import com.datacompare.entity.DataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class QueryGenerator {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String generateQuery(DataSource dataSource, String primaryKeys, String compareFields) {
        try {
            JsonNode config = objectMapper.readTree(dataSource.getConfig());
            
            switch (dataSource.getType()) {
                case DATABASE -> {
                    return generateDatabaseQuery(config, primaryKeys, compareFields);
                }
                case HTTP -> {
                    return generateHttpQuery(config, primaryKeys, compareFields);
                }
                default -> throw new UnsupportedOperationException("不支持的数据源类型");
            }
        } catch (Exception e) {
            throw new RuntimeException("生成查询语句失败: " + e.getMessage());
        }
    }
    
    private String generateDatabaseQuery(JsonNode config, String primaryKeys, String compareFields) {
        String tableName = config.get("tableName").asText();
        String whereClause = config.has("whereClause") ? config.get("whereClause").asText() : "";
        
        StringBuilder query = new StringBuilder("SELECT ");
        
        // 添加主键字段
        query.append(primaryKeys);
        
        // 添加比较字段
        if (compareFields != null && !compareFields.isEmpty()) {
            query.append(", ").append(compareFields);
        }
        
        query.append(" FROM ").append(tableName);
        
        // 添加where条件
        if (!whereClause.isEmpty()) {
            query.append(" WHERE ").append(whereClause);
        }
        
        return query.toString();
    }
    
    private String generateHttpQuery(JsonNode config, String primaryKeys, String compareFields) {
        // HTTP接口的查询参数处理
        StringBuilder queryParams = new StringBuilder();
        queryParams.append("fields=").append(primaryKeys);
        if (compareFields != null && !compareFields.isEmpty()) {
            queryParams.append(",").append(compareFields);
        }
        
        return queryParams.toString();
    }
} 