# Spring AI SQL 集成示例

一个演示 Spring AI 与 SQL 数据库集成的示例项目，实现通过自然语言生成 SQL 查询。

## 主要功能
- 使用 OpenAI 进行自然语言处理
- 自动生成数据库 Schema 和测试数据
- H2 内存数据库集成
- 支持自然语言转 SQL 查询
- 预置用户、商品、订单数据模型

## 技术栈
- Java 21
- Spring Boot 3.4.1
- Spring AI 1.0.0-M5
- H2 Database

## 快速开始

### 前置条件
- JDK 21+
- Maven 3.9+
- OpenAI API Key

### 配置步骤

1. 克隆仓库：
```bash
git clone https://github.com/yourusername/evan-spring-ai-examples.git
cd evan-spring-ai-examples/spring-ai-sql
```
2.设置环境变量（Windows）:
```bash
set OPENAI_API_KEY=your-api-key
```
3.构建项目：
```bash
mvn clean package
```
4.运行应用：
```bash
mvn spring-boot:run
```

## 使用教程
### 初始化数据库
项目已包含自动初始化脚本：
- ``schema.sql`` 创建数据表结构
- ``data.sql`` 插入测试数据

### 访问 H2 控制台：
```plaintext
http://localhost:8080/h2-console
```
#### 配置参数：

- DBC URL: jdbc:h2:mem:testdb
- User: sa
- Password: [空]

### 自然语言转SQL
使用 Postman 或 curl 发送 POST 请求到 `/sql` 接口：
#### API请求
```bash
% curl localhost:8080/sql \
  -H"Content-type: application/json" \
  -d'{"question":"What are the files and file status of SystemA?"}'
```
#### 示例响应:
```json
{
  "sqlQuery": "select ft.file_name, ft.transfer_status \nfrom FileTransfers ft\njoin UpstreamSystems us on ft.upstream_system_id = us.id\nwhere us.name = 'SystemA'",
  "results": [
    {
      "FILE_NAME": "data_20240101.csv",
      "TRANSFER_STATUS": "COMPLETED"
    },
    {
      "FILE_NAME": "data_20240102.csv",
      "TRANSFER_STATUS": "FAILED"
    },
    {
      "FILE_NAME": "data_20240108.csv",
      "TRANSFER_STATUS": "PENDING"
    },
    {
      "FILE_NAME": "data_20240113.csv",
      "TRANSFER_STATUS": "IN_PROGRESS"
    }
  ]
}
```
### 数据模型
```sql
-- 上游系统表
create table UpstreamSystems (
    id int not null auto_increment,
    name varchar(255) not null unique,
    description varchar(255),
    primary key (id)
);

-- 下游系统表
create table DownstreamSystems (
    id int not null auto_increment,
    name varchar(255) not null unique,
    description varchar(255),
    primary key (id)
);

-- SFTP 连接详情表
create table ConnectionDetails (
    id int not null auto_increment,
    host varchar(255) not null,
    port int not null,
    username varchar(255) not null,
    password varchar(255) not null,
    primary key (id)
);

-- 文件传输记录表
create table FileTransfers (
    id int not null auto_increment,
    file_name varchar(255) not null,
    file_size bigint not null,
    transfer_status varchar(50) not null, -- e.g., PENDING, IN_PROGRESS, COMPLETED, FAILED
    transfer_date timestamp default current_timestamp,
    upstream_system_id int not null,
    downstream_system_id int not null,
    connection_detail_id int not null,
    error_message varchar(255),
    primary key (id),
    foreign key (upstream_system_id) references UpstreamSystems(id),
    foreign key (downstream_system_id) references DownstreamSystems(id),
    foreign key (connection_detail_id) references ConnectionDetails(id)
);
```
### 配置说明
#### 修改 application.yml 配置：
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY} # 从环境变量读取
      base-url: https://api.openai.com # 生产环境需修改
      chat:
        options:
          model: gpt-4o # 指定AI模型
```

## 注意事项
- 确保API_KEY存储在安全位置
- H2数据库数据在应用重启后会丢失
- 当前使用Spring AI Milestone版本，API可能发生变化
- 生产环境需替换为持久化数据库

© 2024 Evan Spring AI Examples. MIT License.