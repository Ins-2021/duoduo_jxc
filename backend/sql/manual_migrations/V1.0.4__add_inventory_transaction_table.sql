-- V1.0.4__add_inventory_transaction_table.sql
-- 幂等版本：使用 IF NOT EXISTS 防护

CREATE TABLE IF NOT EXISTS jxc_inventory_transaction (
    transaction_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    warehouse_id   BIGINT NOT NULL COMMENT '仓库ID',
    sku_id         BIGINT NOT NULL COMMENT 'SKU ID',
    trans_type     TINYINT NOT NULL COMMENT '类型：1-入库 2-出库 3-锁定 4-解锁 5-盘点调整',
    qty            INT NOT NULL COMMENT '变动数量',
    before_qty     INT COMMENT '变动前库存',
    after_qty      INT COMMENT '变动后库存',
    bill_type      VARCHAR(30) COMMENT '源单据类型',
    bill_id        BIGINT COMMENT '源单据ID',
    bill_no        VARCHAR(50) COMMENT '源单据编号',
    remark         VARCHAR(255) COMMENT '备注',
    operator_id    BIGINT COMMENT '操作人ID',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by      BIGINT COMMENT '创建人ID',
    update_by      BIGINT COMMENT '更新人ID',
    deleted        INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (transaction_id),
    KEY idx_warehouse_sku (warehouse_id, sku_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';
