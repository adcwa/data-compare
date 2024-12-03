package com.datacompare.controller;

import com.datacompare.dto.DataSourceDTO;
import com.datacompare.entity.DataSource;
import com.datacompare.service.DataSourceService;
import com.datacompare.validator.DataSourceConfigValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datasources")
public class DataSourceController {
    
    @Autowired
    private DataSourceService dataSourceService;
    
    @Autowired
    private DataSourceConfigValidator configValidator;
    
    @PostMapping
    public ResponseEntity<DataSource> createDataSource(@Valid @RequestBody DataSourceDTO dto) {
        DataSource dataSource = new DataSource();
        dataSource.setName(dto.getName());
        dataSource.setType(dto.getType());
        dataSource.setConfig(dto.getConfig());
        
        configValidator.validate(dataSource);
        
        return ResponseEntity.ok(dataSourceService.createDataSource(dataSource));
    }
    
    @GetMapping
    public ResponseEntity<List<DataSource>> getAllDataSources() {
        return ResponseEntity.ok(dataSourceService.getAllDataSources());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DataSource> getDataSource(@PathVariable Long id) {
        return ResponseEntity.ok(dataSourceService.getDataSourceById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DataSource> updateDataSource(
            @PathVariable Long id,
            @Valid @RequestBody DataSourceDTO dto) {
        DataSource dataSource = new DataSource();
        dataSource.setName(dto.getName());
        dataSource.setType(dto.getType());
        dataSource.setConfig(dto.getConfig());
        
        configValidator.validate(dataSource);
        
        return ResponseEntity.ok(dataSourceService.updateDataSource(id, dataSource));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataSource(@PathVariable Long id) {
        dataSourceService.deleteDataSource(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/test")
    public ResponseEntity<String> testConnection(@PathVariable Long id) {
        dataSourceService.testConnection(id);
        return ResponseEntity.ok("连接测试成功");
    }
} 