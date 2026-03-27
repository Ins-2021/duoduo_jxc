# SQL-CHANGELOG

## 2026-03-27 脚本完善 & 幂等化

### 1) inventory_finance_schema.sql 重写

- 从 16 张表扩展到 **22 张表**，新增：
  - `jxc_finance_account`（财务账户）
  - `jxc_inventory_transaction`（库存流水）
  - `jxc_booking_delivery`（预订单发货记录）
  - `jxc_retail_settlement`（零售日结单）
  - `jxc_retail_settlement_order`（日结单关联零售单）
  - `jxc_receivable_source`（应收来源明细）
- **全部改为 `CREATE TABLE IF NOT EXISTS`**，移除 `DROP TABLE IF EXISTS`
- 补充已有表的缺失字段：
  - `jxc_receivable` / `jxc_payable` 增加 `source_type`、`source_id`、`source_doc_no`
  - `jxc_finance_transaction` 增加 `create_by`、`update_by`
- 新增增量迁移段（`ADD COLUMN IF NOT EXISTS`）：
  - `jxc_inventory` 增加 `available_qty`、`locked_qty`
  - `jxc_sales_order_detail` 增加 `delivery_qty`
  - `jxc_purchase_order` 增加 `discount_amount`、`other_fee`、`actual_amount`、`remark`
  - `jxc_purchase_order_detail` 增加 `deleted`、`create_by`、`update_by`
  - `jxc_supplier` 增加 `contact_name`、`contact_phone`、`address`、`initial_arrears`
- 移除 `USE duoduo_jxc` 和 `SET FOREIGN_KEY_CHECKS`，避免污染启动上下文

### 2) application.yml 注册

- `inventory_finance_schema.sql` 加入 `schema-locations`，位于 `sales_return_schema.sql` 之后

### 3) V1.0.x 迁移脚本幂等化

| 脚本 | 变更 |
|---|---|
| `V1.0.1` | `ADD COLUMN` → `ADD COLUMN IF NOT EXISTS`，`CREATE TABLE` → `CREATE TABLE IF NOT EXISTS` |
| `V1.0.2` | 同上 |
| `V1.0.3` | `CREATE TABLE` → `CREATE TABLE IF NOT EXISTS` |
| `V1.0.4` | `CREATE TABLE` → `CREATE TABLE IF NOT EXISTS` |
| `V1.0.5` | `ADD COLUMN` → `ADD COLUMN IF NOT EXISTS` |
| `V1.0.6` | `ADD COLUMN` → `ADD COLUMN IF NOT EXISTS` |

### 4) 当前权威 SQL 加载顺序

`application.yml` → `spring.sql.init.schema-locations`：
1. `workflow_schema.sql` — 工作流表（6张）
2. `rbac_schema.sql` — 系统管理表（9张）
3. `rbac_migration.sql` — RBAC 增量迁移
4. `oauth2_schema.sql` — OAuth2 认证表
5. `sales_return_schema.sql` — 销售退货表（2张）
6. **`inventory_finance_schema.sql`** — 库存+财务表（22张）+ 增量迁移
7. `doc_no_rule_schema.sql` — 单号规则表
8. `product_spu_alter.sql` — 商品SPU字段增量
9. `customer_level_migration.sql` — 客户等级字段增量
10. `product_select_indexes.sql` — 查询性能索引

Data: `rbac_seed.sql`

## 2026-03-26 规范化整理

### 1) 权威 SQL 入口统一

- 权威目录：`backend/src/main/resources/sql/`
- 启动加载由 `application.yml` 统一维护

### 2) 脚本幂等化修正

- `sales_return_schema.sql` — `DROP TABLE + CREATE TABLE` → `CREATE TABLE IF NOT EXISTS`
- `doc_no_rule_schema.sql` — `CREATE TABLE` → `CREATE TABLE IF NOT EXISTS`，数据 → `INSERT IGNORE`
- `product_spu_alter.sql` — `ADD COLUMN` → `ADD COLUMN IF NOT EXISTS`
- `customer_level_migration.sql` — `ALTER TABLE IF EXISTS + ADD COLUMN IF NOT EXISTS`

### 3) manual_migrations 状态

- `MERGED_AND_REMOVED`：`migrate_sales_return_partial.sql`、`alter_customer_columns.sql`
- `MANUAL_KEEP`：`migrate_product_sku_warning_qty.sql`、`add_spu_attr_names.sql`
- `MANUAL_OPTIONAL`：`charset_fix.sql`
- `MANUAL_ONCE`：`seed_print_template.sql`
- `DEPRECATED`：`alter_product_sku_index.sql`、`migrate_route_menu_perms.sql`
- `FORBIDDEN`：`update_user.sql`

### 4) 后续执行规则

- 新增结构变更：写入 `resources/sql/*_migration.sql`，必须幂等
- 新增初始化数据：写入 `resources/sql/*_seed.sql`，必须可重复执行
- 严禁将含 `DROP TABLE` 的脚本加入启动自动加载
