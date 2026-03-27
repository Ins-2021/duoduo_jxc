-- V1.0.1__add_booking_delivery_and_inventory_fields.sql
-- 幂等版本：使用 IF NOT EXISTS 防护

-- 1. jxc_sales_order_detail 新增 delivery_qty 字段
ALTER TABLE jxc_sales_order_detail
    ADD COLUMN IF NOT EXISTS delivery_qty INT NOT NULL DEFAULT 0 COMMENT '已转发货数量（仅预订单有效）' AFTER unfulfilled_qty;

-- 2. jxc_inventory 新增字段
ALTER TABLE jxc_inventory
    ADD COLUMN IF NOT EXISTS available_qty INT NOT NULL DEFAULT 0 COMMENT '可用库存（总库存-已锁定）' AFTER qty,
    ADD COLUMN IF NOT EXISTS locked_qty INT NOT NULL DEFAULT 0 COMMENT '已锁定库存' AFTER available_qty;

-- 初始化：存量数据 available_qty = qty, locked_qty = 0
UPDATE jxc_inventory SET available_qty = qty, locked_qty = 0 WHERE available_qty IS NULL OR available_qty = 0;

-- 3. 新增预订单发货记录表
CREATE TABLE IF NOT EXISTS jxc_booking_delivery (
    delivery_id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '发货记录ID',
    booking_order_id BIGINT NOT NULL COMMENT '预订单ID',
    booking_detail_id BIGINT NOT NULL COMMENT '预订单明细ID',
    convert_qty      INT NOT NULL COMMENT '本次转化数量',
    sales_order_id   BIGINT COMMENT '关联的销售单ID（转单后填写）',
    warehouse_id     BIGINT COMMENT '出库仓库ID',
    operator_id      BIGINT COMMENT '操作人ID',
    create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by        BIGINT COMMENT '创建人ID',
    update_by        BIGINT COMMENT '更新人ID',
    deleted          INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (delivery_id),
    KEY idx_booking_order_id (booking_order_id),
    KEY idx_booking_detail_id (booking_detail_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预订单发货记录表';
