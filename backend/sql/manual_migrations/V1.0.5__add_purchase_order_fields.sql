-- V1.0.5__add_purchase_order_fields.sql
-- 幂等版本：使用 IF NOT EXISTS 防护

ALTER TABLE jxc_purchase_order
    ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '折扣金额' AFTER paid_amount,
    ADD COLUMN IF NOT EXISTS other_fee DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '其它费用' AFTER discount_amount,
    ADD COLUMN IF NOT EXISTS actual_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实际应付总计' AFTER other_fee,
    ADD COLUMN IF NOT EXISTS remark VARCHAR(500) DEFAULT NULL COMMENT '备注' AFTER actual_amount;
