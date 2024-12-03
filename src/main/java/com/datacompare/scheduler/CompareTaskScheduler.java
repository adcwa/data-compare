package com.datacompare.scheduler;

import com.datacompare.entity.CompareTask;
import com.datacompare.repository.CompareTaskRepository;
import com.datacompare.service.CompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompareTaskScheduler {
    
    @Autowired
    private CompareTaskRepository taskRepository;
    
    @Autowired
    private CompareService compareService;
    
    @Scheduled(cron = "0 */5 * * * *") // 每5分钟执行一次
    public void executeScheduledTasks() {
        List<CompareTask> scheduledTasks = taskRepository.findByTriggerType(CompareTask.TriggerType.SCHEDULED);
        
        for (CompareTask task : scheduledTasks) {
            try {
                if (shouldExecuteTask(task)) {
                    compareService.compare(task.getId());
                }
            } catch (Exception e) {
                // TODO: 添加任务执行异常处理逻辑
            }
        }
    }
    
    private boolean shouldExecuteTask(CompareTask task) {
        if (task.getCronExpression() == null || task.getCronExpression().isEmpty()) {
            return false;
        }
        
        // TODO: 根据cron表达式判断任务是否应该执行
        return true;
    }
} 