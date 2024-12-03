package com.datacompare.controller;

import com.datacompare.entity.CompareResult;
import com.datacompare.entity.CompareTask;
import com.datacompare.repository.CompareResultRepository;
import com.datacompare.repository.CompareTaskRepository;
import com.datacompare.service.CompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class CompareTaskController {
    
    @Autowired
    private CompareTaskRepository taskRepository;
    
    @Autowired
    private CompareResultRepository resultRepository;
    
    @Autowired
    private CompareService compareService;
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<CompareResult> executeTask(@PathVariable Long id) {
        CompareResult result = compareService.compare(id);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<CompareResult> getTaskStatus(@PathVariable Long id) {
        CompareTask task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("任务不存在"));
            
        CompareResult latestResult = resultRepository.findFirstByTaskIdOrderByExecutionTimeDesc(id)
            .orElse(null);
            
        return ResponseEntity.ok(latestResult);
    }
    
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<CompareResult>> getTaskHistory(
            @PathVariable Long id,
            Pageable pageable) {
        Page<CompareResult> history = resultRepository.findByTaskIdOrderByExecutionTimeDesc(id, pageable);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompareTask> getTask(@PathVariable Long id) {
        CompareTask task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("任务不存在"));
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<Page<CompareTask>> getAllTasks(Pageable pageable) {
        return ResponseEntity.ok(taskRepository.findAll(pageable));
    }
} 