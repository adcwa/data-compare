package com.datacompare.service;

import com.datacompare.entity.CompareResult;
import com.datacompare.entity.CompareTask;
import com.datacompare.repository.CompareResultRepository;
import com.datacompare.repository.CompareTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CompareService {
    
    @Autowired
    private CompareTaskRepository taskRepository;
    
    @Autowired
    private CompareResultRepository resultRepository;
    
    @Autowired
    private DatabaseConnector databaseConnector;
    
    @Autowired
    private HttpConnector httpConnector;
    
    @Autowired
    private QueryGenerator queryGenerator;
    
    @Autowired
    private ProgressTracker progressTracker;
    
    public CompareResult compare(Long taskId) {
        CompareTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("比较任务不存在"));
            
        CompareResult result = new CompareResult();
        result.setTask(task);
        result.setExecutionTime(LocalDateTime.now());
        result.setStatus(CompareResult.Status.IN_PROGRESS);
        result = resultRepository.save(result);
        
        try {
            // 获取源数据和目标数据
            progressTracker.initProgress(taskId, 0);
            progressTracker.updateProgress(taskId, 0, "正在获取源数据");
            Object sourceData = fetchData(task.getSource(), task.getPrimaryKeys());
            
            progressTracker.updateProgress(taskId, 0, "正在获取目标数据");
            Object targetData = fetchData(task.getTarget(), task.getPrimaryKeys());
            
            // 更新总记录数
            DataSet sourceSet = (DataSet) sourceData;
            progressTracker.initProgress(taskId, sourceSet.getRows().size());
            
            // 执行数据比较
            progressTracker.updateProgress(taskId, 0, "正在比较数据");
            Map<String, Object> diffResult = compareData(sourceData, targetData, 
                task.getPrimaryKeys(), task.getCompareFields());
            
            // 更新比较结果
            result.setStatus(CompareResult.Status.SUCCESS);
            result.setDiffCount((Integer) diffResult.get("diffCount"));
            result.setDetails(objectMapper.writeValueAsString(diffResult.get("details")));
            
            progressTracker.updateDiffCount(taskId, result.getDiffCount());
            progressTracker.updateProgress(taskId, sourceSet.getRows().size(), "比较完成");
            
        } catch (Exception e) {
            result.setStatus(CompareResult.Status.FAILED);
            result.setDetails(e.getMessage());
            progressTracker.updateProgress(taskId, 0, "比较失败: " + e.getMessage());
        } finally {
            progressTracker.removeProgress(taskId);
        }
        
        return resultRepository.save(result);
    }
    
    private Object fetchData(DataSource dataSource, String primaryKeys) {
        String query = generateQuery(dataSource, primaryKeys);
        return switch (dataSource.getType()) {
            case DATABASE -> databaseConnector.fetchData(dataSource.getConfig(), query);
            case HTTP -> httpConnector.fetchData(dataSource.getConfig(), query);
            case FILE -> throw new UnsupportedOperationException("暂不支持文件类型的数据源");
        };
    }
    
    private String generateQuery(DataSource dataSource, String primaryKeys) {
        return queryGenerator.generateQuery(dataSource, primaryKeys, null);
    }
    
    private Map<String, Object> compareData(Object sourceData, Object targetData,
            String primaryKeys, String compareFields) {
        DataSet sourceSet = (DataSet) sourceData;
        DataSet targetSet = (DataSet) targetData;
        
        String[] primaryKeyArray = primaryKeys.split(",");
        String[] compareFieldArray = compareFields.split(",");
        
        Map<String, DataRow> sourceMap = sourceSet.groupByKey(primaryKeyArray[0]);
        Map<String, DataRow> targetMap = targetSet.groupByKey(primaryKeyArray[0]);
        
        List<Map<String, Object>> differences = new ArrayList<>();
        int diffCount = 0;
        
        // 比较源数据中的记录
        for (Map.Entry<String, DataRow> entry : sourceMap.entrySet()) {
            String key = entry.getKey();
            DataRow sourceRow = entry.getValue();
            DataRow targetRow = targetMap.get(key);
            
            if (targetRow == null) {
                // 目标数据中不存在的记录
                differences.add(createDifference("MISSING_IN_TARGET", key, sourceRow, null));
                diffCount++;
            } else {
                // 比较字段值
                Map<String, Object> diff = compareRows(sourceRow, targetRow, compareFieldArray);
                if (diff != null) {
                    differences.add(diff);
                    diffCount++;
                }
            }
        }
        
        // 检查目标数据中多出的记录
        for (String key : targetMap.keySet()) {
            if (!sourceMap.containsKey(key)) {
                differences.add(createDifference("MISSING_IN_SOURCE", key, null, targetMap.get(key)));
                diffCount++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("diffCount", diffCount);
        result.put("details", differences);
        return result;
    }
    
    private Map<String, Object> compareRows(DataRow sourceRow, DataRow targetRow, String[] compareFields) {
        Map<String, Object> fieldDiffs = new HashMap<>();
        boolean hasDiff = false;
        
        for (String field : compareFields) {
            Object sourceValue = sourceRow.getValue(field);
            Object targetValue = targetRow.getValue(field);
            
            if (!Objects.equals(sourceValue, targetValue)) {
                fieldDiffs.put(field, Map.of(
                    "source", sourceValue,
                    "target", targetValue
                ));
                hasDiff = true;
            }
        }
        
        return hasDiff ? fieldDiffs : null;
    }
    
    private Map<String, Object> createDifference(String type, String key, DataRow sourceRow, DataRow targetRow) {
        Map<String, Object> diff = new HashMap<>();
        diff.put("type", type);
        diff.put("key", key);
        diff.put("source", sourceRow != null ? sourceRow.getData() : null);
        diff.put("target", targetRow != null ? targetRow.getData() : null);
        return diff;
    }
} 