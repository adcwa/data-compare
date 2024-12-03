package com.datacompare.service;

import com.datacompare.connector.DatabaseConnector;
import com.datacompare.connector.HttpConnector;
import com.datacompare.entity.DataSource;
import com.datacompare.repository.DataSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataSourceService {
    
    @Autowired
    private DataSourceRepository dataSourceRepository;
    
    @Autowired
    private DatabaseConnector databaseConnector;
    
    @Autowired
    private HttpConnector httpConnector;
    
    @Transactional
    public DataSource createDataSource(DataSource dataSource) {
        if (dataSourceRepository.existsByName(dataSource.getName())) {
            throw new RuntimeException("数据源名称已存在");
        }
        
        dataSource.setCreatedAt(LocalDateTime.now());
        dataSource.setUpdatedAt(LocalDateTime.now());
        return dataSourceRepository.save(dataSource);
    }
    
    public List<DataSource> getAllDataSources() {
        return dataSourceRepository.findAll();
    }
    
    public DataSource getDataSourceById(Long id) {
        return dataSourceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("数据源不存在"));
    }
    
    @Transactional
    public DataSource updateDataSource(Long id, DataSource dataSource) {
        DataSource existingDataSource = getDataSourceById(id);
        existingDataSource.setName(dataSource.getName());
        existingDataSource.setType(dataSource.getType());
        existingDataSource.setConfig(dataSource.getConfig());
        existingDataSource.setUpdatedAt(LocalDateTime.now());
        return dataSourceRepository.save(existingDataSource);
    }
    
    @Transactional
    public void deleteDataSource(Long id) {
        dataSourceRepository.deleteById(id);
    }
    
    public void testConnection(Long id) {
        DataSource dataSource = getDataSourceById(id);
        switch (dataSource.getType()) {
            case DATABASE -> databaseConnector.testConnection(dataSource.getConfig());
            case HTTP -> httpConnector.testConnection(dataSource.getConfig());
            case FILE -> {
                // 文件类型的数据源只需要验证文件是否存在
                // TODO: 实现文件存在性检查
            }
        }
    }
} 