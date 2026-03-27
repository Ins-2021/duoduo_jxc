# SQL-CHANGELOG

## 2026-03-26 规范化整理

### 1) 权威 SQL 入口统一

- 权威目录：`backend/src/main/resources/sql/`
- 启动加载由 `application.yml` 统一维护
- 当前顺序：
  1. `workflow_schema.sql`
  2. `rbac_schema.sql`
  3. `rbac_migration.sql`
  4. `oauth2_schema.sql`
  5. `sales_return_schema.sql`
  6. `doc_no_rule_schema.sql`
  7. `product_spu_alter.sql`
  8. `customer_level_migration.sql`
  9. `rbac_seed.sql`（data-locations）

### 2) 脚本幂等化修正

- `sales_return_schema.sql`
  - 从 `DROP TABLE + CREATE TABLE` 调整为 `CREATE TABLE IF NOT EXISTS`
  - 移除 `USE duoduo_jxc` 和 `FOREIGN_KEY_CHECKS` 切换，避免污染启动上下文
- `doc_no_rule_schema.sql`
  - 表结构改为 `CREATE TABLE IF NOT EXISTS`
  - 初始化数据改为 `INSERT IGNORE`
- `product_spu_alter.sql`
  - 所有字段变更改为 `ADD COLUMN IF NOT EXISTS`
- `customer_level_migration.sql`
  - 新增客户等级字段迁移，采用 `ALTER TABLE IF EXISTS + ADD COLUMN IF NOT EXISTS`

### 3) 历史脚本归档

- `backend/sql/` 仅保留：
  - `init.sql`（历史全量初始化）
  - `README.md`
  - `manual_migrations/`（手工脚本归档）

### 4) manual_migrations 状态

- `MERGED_AND_REMOVED`：`migrate_sales_return_partial.sql`（已并入 `sales_return_schema.sql`）
- `MANUAL_KEEP`：`migrate_product_sku_warning_qty.sql`
- `MERGED_AND_REMOVED`：`alter_customer_columns.sql`（已合并到 `customer_level_migration.sql`）
- `MANUAL_OPTIONAL`：`charset_fix.sql`
- `MANUAL_ONCE`：`seed_print_template.sql`
- `DEPRECATED`：`alter_product_sku_index.sql`、`migrate_route_menu_perms.sql`
- `FORBIDDEN`：`update_user.sql`

### 5) 后续执行规则

- 新增结构变更：写入 `resources/sql/*_migration.sql`，必须幂等
- 新增初始化数据：写入 `resources/sql/*_seed.sql`，必须可重复执行
- 严禁将含 `DROP TABLE` 的脚本加入启动自动加载
