-- V1.0.6__add_purchase_order_detail_fields.sql
-- 幂等版本：使用 IF NOT EXISTS 防护

ALTER TABLE jxc_purchase_order_detail
    ADD COLUMN IF NOT EXISTS deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记' AFTER warehouse_id,
    ADD COLUMN IF NOT EXISTS create_by BIGINT DEFAULT NULL COMMENT '创建人ID' AFTER deleted,
    ADD COLUMN IF NOT EXISTS update_by BIGINT DEFAULT NULL COMMENT '更新人ID' AFTER create_by;
