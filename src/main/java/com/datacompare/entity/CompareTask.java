package com.datacompare.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "compare_task")
public class CompareTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "source_id")
    private DataSource source;
    
    @ManyToOne
    @JoinColumn(name = "target_id")
    private DataSource target;
    
    private String primaryKeys;
    
    private String compareFields;
    
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;
    
    private String cronExpression;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum TriggerType {
        MANUAL, SCHEDULED
    }
} 