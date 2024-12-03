package com.datacompare.repository;

import com.datacompare.entity.CompareResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompareResultRepository extends JpaRepository<CompareResult, Long> {
    List<CompareResult> findByTaskIdOrderByExecutionTimeDesc(Long taskId);
    
    Optional<CompareResult> findFirstByTaskIdOrderByExecutionTimeDesc(Long taskId);
    
    Page<CompareResult> findByTaskIdOrderByExecutionTimeDesc(Long taskId, Pageable pageable);
} 