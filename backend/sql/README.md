# 手工 SQL 目录说明

本目录不再作为 Spring Boot 启动自动加载入口。

## 目录结构

- `init.sql`：历史全量初始化脚本（包含大量 `DROP TABLE`），仅用于全新重建数据库时人工评估后执行
- `manual_migrations/`：历史手工迁移脚本与一次性修复脚本
- `SQL-CHANGELOG.md`：SQL 脚本整理与状态变更记录

## 使用原则

- 日常开发与联调以 `backend/src/main/resources/sql/` 为准
- 需要执行手工 SQL 时，先核对是否已经被权威脚本覆盖
- 执行前先在测试库验证，再在目标库执行
