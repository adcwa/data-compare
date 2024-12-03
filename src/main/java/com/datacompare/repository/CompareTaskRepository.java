package com.datacompare.repository;

import com.datacompare.entity.CompareTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompareTaskRepository extends JpaRepository<CompareTask, Long> {
    List<CompareTask> findByTriggerType(CompareTask.TriggerType triggerType);
} 