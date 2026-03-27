-- V1.0.3__add_retail_settlement_tables.sql
-- 幂等版本：使用 IF NOT EXISTS 防护

CREATE TABLE IF NOT EXISTS jxc_retail_settlement (
    settlement_id   BIGINT NOT NULL AUTO_INCREMENT COMMENT '日结单ID',
    settlement_no  VARCHAR(50) NOT NULL COMMENT '日结单号 RS+yyyyMMdd+seq',
    store_id       BIGINT COMMENT '门店ID',
    settlement_date DATE NOT NULL COMMENT '结算日期',
    cash_amount    DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '现金收款',
    wechat_amount  DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '微信收款',
    alipay_amount  DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '支付宝收款',
    card_amount    DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '银行卡收款',
    other_amount   DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '其他收款',
    total_amount   DECIMAL(12,2) NOT NULL COMMENT '合计收款',
    order_count    INT NOT NULL COMMENT '零售单数量',
    operator_id    BIGINT COMMENT '操作人ID',
    status         TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-作废',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by      BIGINT COMMENT '创建人ID',
    update_by      BIGINT COMMENT '更新人ID',
    deleted        INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (settlement_id),
    KEY idx_settlement_date (settlement_date),
    KEY idx_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='零售日结单表';

CREATE TABLE IF NOT EXISTS jxc_retail_settlement_order (
    id                 BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    settlement_id      BIGINT NOT NULL COMMENT '日结单ID',
    sales_order_id     BIGINT NOT NULL COMMENT '零售单ID',
    sales_order_doc_no VARCHAR(50) COMMENT '零售单号',
    payment_method     VARCHAR(20) COMMENT 'CASH/WECHAT/ALIPAY/CARD/OTHER',
    amount             DECIMAL(12,2) NOT NULL COMMENT '金额',
    create_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by          BIGINT COMMENT '创建人ID',
    update_by          BIGINT COMMENT '更新人ID',
    deleted            INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (id),
    KEY idx_settlement_id (settlement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日结单关联零售单明细表';
