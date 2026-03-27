-- V1.0.2__add_receivable_source_fields.sql
-- 幂等版本：使用 IF NOT EXISTS 防护

-- 1. jxc_receivable 新增来源追溯字段
ALTER TABLE jxc_receivable
    ADD COLUMN IF NOT EXISTS source_type VARCHAR(30) DEFAULT NULL COMMENT '来源单据类型：SALES_ORDER/SALES_RETURN/PURCHASE_ORDER' AFTER remark,
    ADD COLUMN IF NOT EXISTS source_id BIGINT DEFAULT NULL COMMENT '来源单据ID' AFTER source_type,
    ADD COLUMN IF NOT EXISTS source_doc_no VARCHAR(50) DEFAULT NULL COMMENT '来源单据编号' AFTER source_id;

-- 2. jxc_payable 新增来源追溯字段（结构同 Receivable）
ALTER TABLE jxc_payable
    ADD COLUMN IF NOT EXISTS source_type VARCHAR(30) DEFAULT NULL COMMENT '来源单据类型' AFTER remark,
    ADD COLUMN IF NOT EXISTS source_id BIGINT DEFAULT NULL COMMENT '来源单据ID' AFTER source_type,
    ADD COLUMN IF NOT EXISTS source_doc_no VARCHAR(50) DEFAULT NULL COMMENT '来源单据编号' AFTER source_id;

-- 3. 新增应收来源明细表
CREATE TABLE IF NOT EXISTS jxc_receivable_source (
    id              BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    receivable_id   BIGINT NOT NULL COMMENT '应收单ID',
    order_id        BIGINT COMMENT '关联订单ID',
    detail_id       BIGINT COMMENT '关联订单明细ID',
    sku_id          BIGINT COMMENT '商品SKU ID',
    qty             INT COMMENT '数量',
    unit_price      DECIMAL(10,4) COMMENT '单价',
    line_amount     DECIMAL(12,2) COMMENT '金额',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by       BIGINT COMMENT '创建人ID',
    update_by       BIGINT COMMENT '更新人ID',
    deleted         INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (id),
    KEY idx_receivable_id (receivable_id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收来源明细表';
