package com.datacompare.model;

import lombok.Data;

@Data
public class CompareProgress {
    private Long taskId;
    private int totalRecords;
    private int processedRecords;
    private int diffCount;
    private String currentStep;
    private double progressPercentage;
    
    public void updateProgress(int processed) {
        this.processedRecords = processed;
        this.progressPercentage = totalRecords > 0 
            ? (double) processedRecords / totalRecords * 100 
            : 0;
    }
} 