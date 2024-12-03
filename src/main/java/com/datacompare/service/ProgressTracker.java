package com.datacompare.service;

import com.datacompare.model.CompareProgress;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProgressTracker {
    private final Map<Long, CompareProgress> progressMap = new ConcurrentHashMap<>();
    
    public void initProgress(Long taskId, int totalRecords) {
        CompareProgress progress = new CompareProgress();
        progress.setTaskId(taskId);
        progress.setTotalRecords(totalRecords);
        progress.setCurrentStep("初始化");
        progressMap.put(taskId, progress);
    }
    
    public void updateProgress(Long taskId, int processedRecords, String currentStep) {
        CompareProgress progress = progressMap.get(taskId);
        if (progress != null) {
            progress.setProcessedRecords(processedRecords);
            progress.setCurrentStep(currentStep);
            progress.updateProgress(processedRecords);
        }
    }
    
    public void updateDiffCount(Long taskId, int diffCount) {
        CompareProgress progress = progressMap.get(taskId);
        if (progress != null) {
            progress.setDiffCount(diffCount);
        }
    }
    
    public CompareProgress getProgress(Long taskId) {
        return progressMap.get(taskId);
    }
    
    public void removeProgress(Long taskId) {
        progressMap.remove(taskId);
    }
} 