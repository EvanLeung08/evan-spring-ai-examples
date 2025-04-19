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
  -d'{"question":"How many orders do alice have?"}'
```
#### 示例响应:
请帮我查询用户alice的所有订单
```json
{
  "sqlQuery": "select count(*) as order_count from Orders o join Users u on o.user_ref = u.id where u.username = 'alice';",
  "results": [
    {
      "ORDER_COUNT": 2
    }
  ]
}
```
### 数据模型
```sql
-- 用户表
Users (id, username, email)

-- 商品表 
Products (id, name, price, stock)

-- 订单表
Orders (id, user_ref, product_ref, quantity)
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