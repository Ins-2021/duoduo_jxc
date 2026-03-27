-- ============================================================
-- 进销存系统 - 库存模块 & 财务模块 建表脚本
-- 数据库: duoduo_jxc
-- 日期: 2026-03-25
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `duoduo_jxc`;

-- -----------------------------------------------------------
-- 1. 调拨单主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_transfer_order`;
CREATE TABLE `jxc_transfer_order` (
  `transfer_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '调拨单ID',
  `transfer_no` VARCHAR(64) NOT NULL COMMENT '调拨单号',
  `from_warehouse_id` BIGINT DEFAULT NULL COMMENT '调出仓库ID',
  `to_warehouse_id` BIGINT DEFAULT NULL COMMENT '调入仓库ID',
  `from_warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '调出仓库名称',
  `to_warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '调入仓库名称',
  `status` INT DEFAULT 0 COMMENT '状态 0-待审核 1-已审核 2-运输中 3-已完成 4-已取消',
  `transfer_date` DATETIME DEFAULT NULL COMMENT '调拨日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_transfer_no` (`transfer_no`),
  KEY `idx_from_warehouse` (`from_warehouse_id`),
  KEY `idx_to_warehouse` (`to_warehouse_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='调拨单';

-- -----------------------------------------------------------
-- 2. 调拨单明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_transfer_order_detail`;
CREATE TABLE `jxc_transfer_order_detail` (
  `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `transfer_id` BIGINT NOT NULL COMMENT '调拨单ID',
  `sku_id` BIGINT DEFAULT NULL COMMENT 'SKU ID',
  `sku_code` VARCHAR(64) DEFAULT NULL COMMENT 'SKU编码',
  `sku_name` VARCHAR(200) DEFAULT NULL COMMENT 'SKU名称',
  `attr1` VARCHAR(100) DEFAULT NULL COMMENT '属性1',
  `attr2` VARCHAR(100) DEFAULT NULL COMMENT '属性2',
  `qty` INT DEFAULT NULL COMMENT '数量',
  `cost_price` DECIMAL(18,2) DEFAULT NULL COMMENT '成本价',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`detail_id`),
  KEY `idx_transfer_id` (`transfer_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='调拨单明细';

-- -----------------------------------------------------------
-- 3. 库存盘点主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_inventory_check`;
CREATE TABLE `jxc_inventory_check` (
  `check_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '盘点ID',
  `check_no` VARCHAR(64) NOT NULL COMMENT '盘点单号',
  `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
  `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
  `status` INT DEFAULT 0 COMMENT '状态 0-待盘点 1-盘点中 2-已完成',
  `check_date` DATETIME DEFAULT NULL COMMENT '盘点日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`check_id`),
  UNIQUE KEY `uk_check_no` (`check_no`),
  KEY `idx_warehouse` (`warehouse_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存盘点';

-- -----------------------------------------------------------
-- 4. 库存盘点明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_inventory_check_detail`;
CREATE TABLE `jxc_inventory_check_detail` (
  `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `check_id` BIGINT NOT NULL COMMENT '盘点ID',
  `sku_id` BIGINT DEFAULT NULL COMMENT 'SKU ID',
  `sku_code` VARCHAR(64) DEFAULT NULL COMMENT 'SKU编码',
  `sku_name` VARCHAR(200) DEFAULT NULL COMMENT 'SKU名称',
  `attr1` VARCHAR(100) DEFAULT NULL COMMENT '属性1',
  `attr2` VARCHAR(100) DEFAULT NULL COMMENT '属性2',
  `system_qty` INT DEFAULT NULL COMMENT '系统库存数量',
  `actual_qty` INT DEFAULT NULL COMMENT '实际盘点数量',
  `diff_qty` INT DEFAULT NULL COMMENT '差异数量',
  `cost_price` DECIMAL(18,2) DEFAULT NULL COMMENT '成本价',
  `diff_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '差异金额',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`detail_id`),
  KEY `idx_check_id` (`check_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存盘点明细';

-- -----------------------------------------------------------
-- 5. 组装拆卸主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_assembly_order`;
CREATE TABLE `jxc_assembly_order` (
  `assembly_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '组装拆卸ID',
  `assembly_no` VARCHAR(64) NOT NULL COMMENT '组装拆卸单号',
  `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
  `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
  `type` INT DEFAULT NULL COMMENT '类型 1-组装 2-拆卸',
  `status` INT DEFAULT 0 COMMENT '状态 0-待审核 1-已完成',
  `assembly_date` DATETIME DEFAULT NULL COMMENT '操作日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`assembly_id`),
  UNIQUE KEY `uk_assembly_no` (`assembly_no`),
  KEY `idx_warehouse` (`warehouse_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组装拆卸单';

-- -----------------------------------------------------------
-- 6. 组装拆卸明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_assembly_order_detail`;
CREATE TABLE `jxc_assembly_order_detail` (
  `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `assembly_id` BIGINT NOT NULL COMMENT '组装拆卸ID',
  `sku_id` BIGINT DEFAULT NULL COMMENT 'SKU ID',
  `sku_code` VARCHAR(64) DEFAULT NULL COMMENT 'SKU编码',
  `sku_name` VARCHAR(200) DEFAULT NULL COMMENT 'SKU名称',
  `attr1` VARCHAR(100) DEFAULT NULL COMMENT '属性1',
  `attr2` VARCHAR(100) DEFAULT NULL COMMENT '属性2',
  `qty` INT DEFAULT NULL COMMENT '数量',
  `cost_price` DECIMAL(18,2) DEFAULT NULL COMMENT '成本价',
  `item_type` INT DEFAULT NULL COMMENT '明细类型 1-成品 2-子件',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`detail_id`),
  KEY `idx_assembly_id` (`assembly_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组装拆卸明细';

-- -----------------------------------------------------------
-- 7. 其他出入库主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_stock_in_out`;
CREATE TABLE `jxc_stock_in_out` (
  `in_out_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '出入库ID',
  `bill_no` VARCHAR(64) NOT NULL COMMENT '单据编号',
  `type` INT DEFAULT NULL COMMENT '类型 1-入库 2-出库',
  `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
  `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
  `status` INT DEFAULT 0 COMMENT '状态 0-待审核 1-已完成',
  `bill_date` DATETIME DEFAULT NULL COMMENT '单据日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`in_out_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_warehouse` (`warehouse_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='其他出入库';

-- -----------------------------------------------------------
-- 8. 其他出入库明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_stock_in_out_detail`;
CREATE TABLE `jxc_stock_in_out_detail` (
  `detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `in_out_id` BIGINT NOT NULL COMMENT '出入库ID',
  `sku_id` BIGINT DEFAULT NULL COMMENT 'SKU ID',
  `sku_code` VARCHAR(64) DEFAULT NULL COMMENT 'SKU编码',
  `sku_name` VARCHAR(200) DEFAULT NULL COMMENT 'SKU名称',
  `attr1` VARCHAR(100) DEFAULT NULL COMMENT '属性1',
  `attr2` VARCHAR(100) DEFAULT NULL COMMENT '属性2',
  `qty` INT DEFAULT NULL COMMENT '数量',
  `cost_price` DECIMAL(18,2) DEFAULT NULL COMMENT '成本价',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '金额',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`detail_id`),
  KEY `idx_in_out_id` (`in_out_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='其他出入库明细';

-- -----------------------------------------------------------
-- 9. 库存预警表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_inventory_alert`;
CREATE TABLE `jxc_inventory_alert` (
  `alert_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
  `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
  `sku_id` BIGINT DEFAULT NULL COMMENT 'SKU ID',
  `sku_code` VARCHAR(64) DEFAULT NULL COMMENT 'SKU编码',
  `sku_name` VARCHAR(200) DEFAULT NULL COMMENT 'SKU名称',
  `attr1` VARCHAR(100) DEFAULT NULL COMMENT '属性1',
  `attr2` VARCHAR(100) DEFAULT NULL COMMENT '属性2',
  `current_qty` INT DEFAULT NULL COMMENT '当前库存',
  `min_qty` INT DEFAULT NULL COMMENT '最小库存',
  `max_qty` INT DEFAULT NULL COMMENT '最大库存',
  `alert_type` INT DEFAULT NULL COMMENT '预警类型 1-库存不足 2-库存积压',
  `status` INT DEFAULT 0 COMMENT '状态 0-未处理 1-已处理',
  `alert_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预警时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`alert_id`),
  KEY `idx_warehouse` (`warehouse_id`),
  KEY `idx_sku` (`sku_id`),
  KEY `idx_alert_type` (`alert_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存预警';

-- ===========================================================
-- 财务模块
-- ===========================================================

-- -----------------------------------------------------------
-- 10. 应收账款表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_receivable`;
CREATE TABLE `jxc_receivable` (
  `receivable_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '应收ID',
  `bill_no` VARCHAR(64) NOT NULL COMMENT '单据编号',
  `customer_id` BIGINT DEFAULT NULL COMMENT '客户ID',
  `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '应收金额',
  `received_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '已收金额',
  `remaining_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '剩余金额',
  `status` INT DEFAULT 0 COMMENT '状态 0-未核销 1-部分核销 2-已核销',
  `bill_date` DATETIME DEFAULT NULL COMMENT '单据日期',
  `due_date` DATETIME DEFAULT NULL COMMENT '到期日',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`receivable_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应收账款';

-- -----------------------------------------------------------
-- 11. 应付账款表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_payable`;
CREATE TABLE `jxc_payable` (
  `payable_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '应付ID',
  `bill_no` VARCHAR(64) NOT NULL COMMENT '单据编号',
  `supplier_id` BIGINT DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '应付金额',
  `paid_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '已付金额',
  `remaining_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '剩余金额',
  `status` INT DEFAULT 0 COMMENT '状态 0-未核销 1-部分核销 2-已核销',
  `bill_date` DATETIME DEFAULT NULL COMMENT '单据日期',
  `due_date` DATETIME DEFAULT NULL COMMENT '到期日',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`payable_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应付账款';

-- -----------------------------------------------------------
-- 12. 收款单表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_receipt`;
CREATE TABLE `jxc_receipt` (
  `receipt_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收款单ID',
  `receipt_no` VARCHAR(64) NOT NULL COMMENT '收款单号',
  `customer_id` BIGINT DEFAULT NULL COMMENT '客户ID',
  `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
  `account_id` BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name` VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '收款金额',
  `pay_method` VARCHAR(50) DEFAULT NULL COMMENT '收款方式',
  `status` INT DEFAULT 0 COMMENT '状态 0-待审核 1-已审核',
  `receipt_date` DATETIME DEFAULT NULL COMMENT '收款日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`receipt_id`),
  UNIQUE KEY `uk_receipt_no` (`receipt_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收款单';

-- -----------------------------------------------------------
-- 13. 付款单表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_payment`;
CREATE TABLE `jxc_payment` (
  `payment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '付款单ID',
  `payment_no` VARCHAR(64) NOT NULL COMMENT '付款单号',
  `supplier_id` BIGINT DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
  `account_id` BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name` VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '付款金额',
  `pay_method` VARCHAR(50) DEFAULT NULL COMMENT '付款方式',
  `status` INT DEFAULT 0 COMMENT '状态 0-待审核 1-已审核',
  `payment_date` DATETIME DEFAULT NULL COMMENT '付款日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='付款单';

-- -----------------------------------------------------------
-- 14. 资金流水表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_finance_transaction`;
CREATE TABLE `jxc_finance_transaction` (
  `transaction_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `transaction_no` VARCHAR(64) NOT NULL COMMENT '流水号',
  `account_id` BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name` VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `type` INT DEFAULT NULL COMMENT '类型 1-收入 2-支出',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '金额',
  `balance` DECIMAL(18,2) DEFAULT NULL COMMENT '余额',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '收支分类',
  `bill_type` VARCHAR(50) DEFAULT NULL COMMENT '关联单据类型',
  `bill_no` VARCHAR(64) DEFAULT NULL COMMENT '关联单据编号',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `transaction_date` DATETIME DEFAULT NULL COMMENT '交易日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_account` (`account_id`),
  KEY `idx_type` (`type`),
  KEY `idx_transaction_date` (`transaction_date`),
  KEY `idx_bill_no` (`bill_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金流水';

-- -----------------------------------------------------------
-- 15. 核销单表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_write_off`;
CREATE TABLE `jxc_write_off` (
  `write_off_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '核销ID',
  `write_off_no` VARCHAR(64) NOT NULL COMMENT '核销单号',
  `type` VARCHAR(20) DEFAULT NULL COMMENT '核销类型 receivable-应收核销 payable-应付核销',
  `bill_no` VARCHAR(64) DEFAULT NULL COMMENT '关联应收/应付单号',
  `bill_id` BIGINT DEFAULT NULL COMMENT '关联应收/应付ID',
  `receipt_payment_id` BIGINT DEFAULT NULL COMMENT '关联收款/付款单ID',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '核销金额',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`write_off_id`),
  UNIQUE KEY `uk_write_off_no` (`write_off_no`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='核销单';

-- -----------------------------------------------------------
-- 16. 收支记录表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `jxc_income_expense`;
CREATE TABLE `jxc_income_expense` (
  `ie_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收支ID',
  `ie_no` VARCHAR(64) NOT NULL COMMENT '收支单号',
  `type` INT DEFAULT NULL COMMENT '类型 1-收入 2-支出',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `category_name` VARCHAR(100) DEFAULT NULL COMMENT '分类名称',
  `account_id` BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name` VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `amount` DECIMAL(18,2) DEFAULT NULL COMMENT '金额',
  `bill_type` VARCHAR(50) DEFAULT NULL COMMENT '关联单据类型',
  `bill_no` VARCHAR(64) DEFAULT NULL COMMENT '关联单据编号',
  `status` INT DEFAULT 0 COMMENT '状态 0-待审核 1-已审核',
  `bill_date` DATETIME DEFAULT NULL COMMENT '收支日期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`ie_id`),
  UNIQUE KEY `uk_ie_no` (`ie_no`),
  KEY `idx_type` (`type`),
  KEY `idx_category` (`category_id`),
  KEY `idx_account` (`account_id`),
  KEY `idx_bill_date` (`bill_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收支记录';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 建表完成: 16张表 (库存模块9张 + 财务模块7张)
-- ============================================================
