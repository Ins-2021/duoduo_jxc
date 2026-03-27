-- V1.0.7__add_finance_account_audit_fields.sql
-- 为 jxc_finance_account 添加 create_by 和 update_by 审计字段
-- FinanceAccount 实体继承了 BaseEntity，需要对应的数据库列

ALTER TABLE jxc_finance_account ADD COLUMN IF NOT EXISTS create_by BIGINT DEFAULT NULL COMMENT '创建人ID' AFTER update_time;
ALTER TABLE jxc_finance_account ADD COLUMN IF NOT EXISTS update_by BIGINT DEFAULT NULL COMMENT '更新人ID' AFTER create_by;
