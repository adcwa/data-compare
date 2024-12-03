# 数据比较系统

一个用于比较不同数据源之间数据差异的系统。

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Data JPA
- MySQL 8.0
- WebSocket

### 前端
- Vue 3
- TypeScript
- Element Plus
- ECharts
- Pinia
- Vue Router

## 开发环境准备

### 1. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE data_compare DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权
CREATE USER 'data_compare'@'localhost' IDENTIFIED BY 'data_compare123';
GRANT ALL PRIVILEGES ON data_compare. TO 'data_compare'@'localhost';
FLUSH PRIVILEGES;

-- 初始化表结构
USE data_compare;
CREATE TABLE data_source (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
type VARCHAR(20) NOT NULL,
config JSON NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE compare_task (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
source_id BIGINT NOT NULL,
target_id BIGINT NOT NULL,
primary_keys VARCHAR(255) NOT NULL,
compare_fields VARCHAR(1000) NOT NULL,
trigger_type VARCHAR(20) NOT NULL,
cron_expression VARCHAR(100),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
FOREIGN KEY (source_id) REFERENCES data_source(id),
FOREIGN KEY (target_id) REFERENCES data_source(id)
);

CREATE TABLE compare_result (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
task_id BIGINT NOT NULL,
execution_time TIMESTAMP NOT NULL,
status VARCHAR(20) NOT NULL,
diff_count INT NOT NULL DEFAULT 0,
details JSON,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (task_id) REFERENCES compare_task(id)
);
```

### 2. 后端配置

1. 配置application.yml
```
yaml
spring:
datasource:
url: jdbc:mysql://localhost:3306/data_compare?useSSL=false&serverTimezone=UTC
username: data_compare
password: data_compare123
jpa:
hibernate:
ddl-auto: validate
show-sql: true
server:
port: 8080
```
2. 启动后端服务
```bash
cd backend
./mvnw spring-boot:run
```
### 3. 前端配置

1. 安装依赖
```
bash
cd frontend
npm install
```
2. 配置环境变量
创建 `.env.development` 文件：
```plaintext
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=ws://localhost:8080/ws
```


3. 启动开发服务器
```bash
npm run dev
```

## 部署说明

### 后端部署
1. 打包
```bash
cd backend
./mvnw clean package
```
2. 运行
```bash
java -jar target/data-compare-0.1.0.jar
```
### 前端部署
1. 打包
```bash
cd frontend
npm run build

```

2. 将`dist`目录下的文件部署到Web服务器

## 功能说明

1. 数据源管理
   - 支持数据库、HTTP接口、文件等多种数据源
   - 数据源连接测试
   - 配置管理

2. 比较任务
   - 创建和管理比较任务
   - 手动/定时触发
   - 实时进度展示
   - 差异结果可视化

3. 执行历史
   - 任务执行记录
   - 差异详情查看
   - 数据导出

## 开发规范

1. Git提交规范
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式化
refactor: 重构
test: 测试相关
chore: 构建/工具相关


2. 代码风格
- 后端遵循阿里巴巴Java开发规范
- 前端使用ESLint + Prettier
- 提交前执行代码格式化

## 注意事项

1. 数据库
- 建议使用MySQL 8.0或以上版本
- 需要启用JSON数据类型支持

2. 安全性
- 生产环境需要配置HTTPS
- 需要实现适当的认证和授权机制
- 敏感配置信息需要加密存储

3. 性能
- 大数据量比较时注意内存使用
- 建议配置数据库连接池
- 考虑使用缓存优化性能