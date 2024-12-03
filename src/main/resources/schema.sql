-- 数据源配置表
CREATE TABLE data_source (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- DATABASE, HTTP, FILE
    config JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 比较任务配置表
CREATE TABLE compare_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    source_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    primary_keys VARCHAR(255) NOT NULL,
    compare_fields VARCHAR(1000) NOT NULL,
    trigger_type VARCHAR(20) NOT NULL, -- MANUAL, SCHEDULED
    cron_expression VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (source_id) REFERENCES data_source(id),
    FOREIGN KEY (target_id) REFERENCES data_source(id)
);

-- 比较结果表
CREATE TABLE compare_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    execution_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    diff_count INT NOT NULL DEFAULT 0,
    details JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES compare_task(id)
); 