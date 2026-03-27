# manual_migrations 说明

本目录保存历史手工迁移脚本，默认不自动执行。

## 脚本状态清单

### 版本化迁移脚本（已幂等化）

| 脚本 | 状态标签 | 说明 |
|---|---|---|
| `V1.0.0__add_finance_transaction_table.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，使用 `CREATE TABLE IF NOT EXISTS` |
| `V1.0.0__add_receivable_table.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，使用 `CREATE TABLE IF NOT EXISTS` |
| `V1.0.1__add_booking_delivery_and_inventory_fields.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，含幂等 `ADD COLUMN IF NOT EXISTS` |
| `V1.0.2__add_receivable_source_fields.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，含 receivable_source 表 |
| `V1.0.3__add_retail_settlement_tables.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，含零售日结两张表 |
| `V1.0.4__add_inventory_transaction_table.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，含库存流水表 |
| `V1.0.5__add_purchase_order_fields.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，含幂等 `ADD COLUMN IF NOT EXISTS` |
| `V1.0.6__add_purchase_order_detail_fields.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，含幂等 `ADD COLUMN IF NOT EXISTS` |
| `V1.0.7__add_finance_account_audit_fields.sql` | `MERGED` | 已合并到 `inventory_finance_schema.sql`，finance_account 含完整字段 |

### 非版本化脚本

| 脚本 | 状态标签 | 结论 |
|---|---|---|
| `add_spu_attr_names.sql` | `MANUAL_KEEP` | 幂等，补充 SPU 属性名 |
| `alter_product_sku_index.sql` | `DEPRECATED` | 与 `migrate_product_sku_warning_qty.sql` 职责重复，且非幂等，不建议执行 |
| `charset_fix.sql` | `MANUAL_OPTIONAL` | 环境修复脚本，仅在字符集异常时人工执行 |
| `migrate_product_sku_warning_qty.sql` | `MANUAL_KEEP` | 幂等脚本，可用于存量库修复 `warning_qty` 与 `idx_sku_code` |
| `migrate_route_menu_perms.sql` | `DEPRECATED` | 多数权限已并入 `rbac_seed.sql`，不建议再单独执行 |
| `seed_print_template.sql` | `MANUAL_ONCE` | 一次性数据脚本，执行前建议改为幂等插入 |
| `update_user.sql` | `FORBIDDEN` | 含 `DROP TABLE`，破坏性强，禁止在任何已有环境执行 |

## 维护约定

- 权威 SQL 脚本统一存放在 `backend/src/main/resources/sql/` 目录
- 所有 DDL 必须幂等（`CREATE TABLE IF NOT EXISTS` / `ADD COLUMN IF NOT EXISTS`）
- 所有 DML 种子数据使用 `INSERT IGNORE` 保证可重复执行
- 本目录仅用于保留历史脚本和紧急修复脚本
- 每次脚本状态变化同步更新本文件和 `backend/sql/SQL-CHANGELOG.md`

## 已完成合并

- `alter_customer_columns.sql` 已合并为权威脚本 `customer_level_migration.sql`，原文件已移除
- `migrate_sales_return_partial.sql` 已并入 `sales_return_schema.sql`，原文件已移除
- `V1.0.0` ~ `V1.0.7` 全部合并到 `inventory_finance_schema.sql`，保留原文件供历史追溯
