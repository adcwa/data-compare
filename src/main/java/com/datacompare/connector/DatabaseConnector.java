package com.datacompare.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.datacompare.model.DataRow;
import com.datacompare.model.DataSet;

@Component
public class DatabaseConnector implements DataSourceConnector {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void testConnection(String config) {
        try {
            JsonNode configNode = objectMapper.readTree(config);
            Class.forName(configNode.get("driverClassName").asText());
            try (Connection conn = DriverManager.getConnection(
                    configNode.get("url").asText(),
                    configNode.get("username").asText(),
                    configNode.get("password").asText())) {
                if (!conn.isValid(5)) {
                    throw new RuntimeException("数据库连接测试失败");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("数据库连接测试失败: " + e.getMessage());
        }
    }
    
    @Override
    public Object fetchData(String config, String query) {
        DataSet dataSet = new DataSet();
        try {
            JsonNode configNode = objectMapper.readTree(config);
            try (Connection conn = DriverManager.getConnection(
                    configNode.get("url").asText(),
                    configNode.get("username").asText(),
                    configNode.get("password").asText());
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                // 获取字段列表
                for (int i = 1; i <= columnCount; i++) {
                    dataSet.getFields().add(metaData.getColumnName(i));
                }
                
                // 获取数据
                while (rs.next()) {
                    DataRow row = new DataRow();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.setValue(columnName, value);
                    }
                    dataSet.addRow(row);
                }
            }
            return dataSet;
        } catch (Exception e) {
            throw new RuntimeException("获取数据库数据失败: " + e.getMessage());
        }
    }
} 