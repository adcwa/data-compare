package com.datacompare.connector;

public interface DataSourceConnector {
    void testConnection(String config);
    Object fetchData(String config, String query);
} 