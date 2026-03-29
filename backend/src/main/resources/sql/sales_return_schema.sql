-- ============================================================
-- 销售退货单 建表脚本
-- ============================================================

-- SET NAMES utf8mb4;

-- -----------------------------------------------------------
-- 1. 销售退货主表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_sales_return_order` (
  `order_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '退货单ID',
  `doc_no` VARCHAR(64) NOT NULL COMMENT '单据编号',
  `doc_date` DATETIME DEFAULT NULL COMMENT '单据日期',
  `customer_id` BIGINT DEFAULT NULL COMMENT '客户ID',
  `origin_sales_id` BIGINT DEFAULT NULL COMMENT '原销售单ID',
  `status` INT DEFAULT 10 COMMENT '状态 10-草稿 20-已审核',
  `refund_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '退款金额',
  `refund_method` VARCHAR(50) DEFAULT NULL COMMENT '退款方式',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_doc_no` (`doc_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='销售退货单';

-- -----------------------------------------------------------
-- 2. 销售退货明细表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_sales_return_detail` (
  `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` BIGINT NOT NULL COMMENT '退货单ID',
  `origin_sales_detail_id` BIGINT DEFAULT NULL COMMENT '原销售明细ID',
  `sku_id` BIGINT DEFAULT NULL COMMENT 'SKU ID',
  `spu_id` BIGINT DEFAULT NULL COMMENT 'SPU ID',
  `origin_qty` INT DEFAULT NULL COMMENT '原销售数量快照',
  `qty` INT DEFAULT NULL COMMENT '退货数量',
  `unit_price` DECIMAL(18,2) DEFAULT NULL COMMENT '退货单价',
  `line_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '行总额',
  `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`detail_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_origin_sales_detail_id` (`origin_sales_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='销售退货明细';
