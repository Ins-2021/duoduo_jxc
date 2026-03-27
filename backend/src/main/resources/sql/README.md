# SQL 脚本整理说明（权威入口）

本目录是后端运行时的权威 SQL 目录，Spring Boot 启动仅应从这里加载脚本。

## 启动加载顺序（application.yml）

1. `workflow_schema.sql`
2. `rbac_schema.sql`
3. `rbac_migration.sql`
4. `oauth2_schema.sql`
5. `sales_return_schema.sql`
6. `doc_no_rule_schema.sql`
7. `product_spu_alter.sql`
8. `customer_level_migration.sql`
9. `rbac_seed.sql`（data-locations）

## 分类约定

- `*_schema.sql`：建表或基础结构脚本，必须可重复执行（幂等）
- `*_migration.sql`：增量变更脚本，必须可重复执行（幂等）
- `*_seed.sql`：初始化数据脚本，建议使用 `INSERT IGNORE` 或 UPSERT

## 当前状态

- 已将 `sales_return_schema.sql` 改为 `CREATE TABLE IF NOT EXISTS`，避免启动时 `DROP TABLE`
- 已将 `doc_no_rule_schema.sql` 改为幂等插入（`INSERT IGNORE`）
- 已将 `product_spu_alter.sql` 改为 `ADD COLUMN IF NOT EXISTS`
- 已新增 `customer_level_migration.sql`，将客户等级字段变更纳入权威入口

## 维护规则

- 新增脚本统一放在本目录
- 禁止在运行时入口使用包含 `DROP TABLE` 的脚本
- 手工运维脚本不放本目录，统一放到 `backend/sql/manual_migrations/`
