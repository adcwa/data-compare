package com.datacompare.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpConnector implements DataSourceConnector {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public void testConnection(String config) {
        try {
            JsonNode configNode = objectMapper.readTree(config);
            String url = configNode.get("url").asText();
            HttpMethod method = HttpMethod.valueOf(configNode.get("method").asText());
            
            HttpHeaders headers = new HttpHeaders();
            if (configNode.has("headers")) {
                configNode.get("headers").fields().forEachRemaining(entry -> 
                    headers.add(entry.getKey(), entry.getValue().asText())
                );
            }
            
            restTemplate.exchange(url, method, new HttpEntity<>(headers), String.class);
        } catch (Exception e) {
            throw new RuntimeException("HTTP接口连接测试失败: " + e.getMessage());
        }
    }
    
    @Override
    public Object fetchData(String config, String query) {
        // TODO: 实现数据获取逻辑
        return null;
    }
} 