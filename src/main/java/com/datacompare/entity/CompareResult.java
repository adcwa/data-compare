package com.datacompare.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "compare_result")
public class CompareResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private CompareTask task;
    
    private LocalDateTime executionTime;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    private Integer diffCount;
    
    @Column(columnDefinition = "json")
    private String details;
    
    private LocalDateTime createdAt;
    
    public enum Status {
        SUCCESS, FAILED, IN_PROGRESS
    }
} 