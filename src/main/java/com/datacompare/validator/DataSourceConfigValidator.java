package com.datacompare.validator;

import com.datacompare.entity.DataSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class DataSourceConfigValidator {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void validate(DataSource dataSource) {
        try {
            JsonNode config = objectMapper.readTree(dataSource.getConfig());
            switch (dataSource.getType()) {
                case DATABASE -> validateDatabaseConfig(config);
                case HTTP -> validateHttpConfig(config);
                case FILE -> validateFileConfig(config);
            }
        } catch (Exception e) {
            throw new RuntimeException("数据源配置格式无效: " + e.getMessage());
        }
    }
    
    private void validateDatabaseConfig(JsonNode config) {
        requireFields(config, "url", "username", "password", "driverClassName");
    }
    
    private void validateHttpConfig(JsonNode config) {
        requireFields(config, "url", "method");
    }
    
    private void validateFileConfig(JsonNode config) {
        requireFields(config, "path", "format");
    }
    
    private void requireFields(JsonNode config, String... fields) {
        for (String field : fields) {
            if (!config.has(field) || config.get(field).isNull()) {
                throw new RuntimeException("缺少必需的配置项: " + field);
            }
        }
    }
} 