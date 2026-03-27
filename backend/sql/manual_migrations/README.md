# manual_migrations 说明

本目录保存历史手工迁移脚本，默认不自动执行。

## 脚本状态清单

| 脚本 | 状态标签 | 结论 |
|---|---|---|
| `alter_product_sku_index.sql` | `DEPRECATED` | 与 `migrate_product_sku_warning_qty.sql` 职责重复，且非幂等，不建议执行 |
| `charset_fix.sql` | `MANUAL_OPTIONAL` | 环境修复脚本，仅在字符集异常时人工执行 |
| `migrate_product_sku_warning_qty.sql` | `MANUAL_KEEP` | 幂等脚本，可用于存量库修复 `warning_qty` 与 `idx_sku_code` |
| `migrate_route_menu_perms.sql` | `DEPRECATED` | 多数权限已并入 `rbac_seed.sql`，不建议再单独执行 |
| `seed_print_template.sql` | `MANUAL_ONCE` | 一次性数据脚本，执行前建议改为幂等插入 |
| `update_user.sql` | `FORBIDDEN` | 含 `DROP TABLE`，破坏性强，禁止在任何已有环境执行 |

## 维护约定

- 新增 SQL 统一进入 `backend/src/main/resources/sql/` 并保证幂等
- 本目录仅用于保留历史脚本和紧急修复脚本
- 每次脚本状态变化同步更新本文件和 `backend/sql/SQL-CHANGELOG.md`

## 已完成合并

- `alter_customer_columns.sql` 已合并为权威脚本 `customer_level_migration.sql`，原文件已移除
- `migrate_sales_return_partial.sql` 已并入 `sales_return_schema.sql`，原文件已移除
