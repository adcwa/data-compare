package com.datacompare.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "data_source")
public class DataSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Enumerated(EnumType.STRING)
    private SourceType type;
    
    @Column(columnDefinition = "json")
    private String config;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum SourceType {
        DATABASE, HTTP, FILE
    }
} 