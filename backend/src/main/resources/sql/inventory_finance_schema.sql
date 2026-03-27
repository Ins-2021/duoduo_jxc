-- ============================================================
-- 进销存系统 - 库存模块 & 财务模块 建表脚本（幂等版）
-- 数据库: duoduo_jxc
-- 日期: 2026-03-27
-- 说明: 所有 CREATE TABLE 均使用 IF NOT EXISTS，可安全重复执行
-- ============================================================

-- ======================== 库存模块 ========================

-- -----------------------------------------------------------
-- 1. 调拨单主表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_transfer_order` (
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
CREATE TABLE IF NOT EXISTS `jxc_transfer_order_detail` (
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
CREATE TABLE IF NOT EXISTS `jxc_inventory_check` (
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
CREATE TABLE IF NOT EXISTS `jxc_inventory_check_detail` (
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
CREATE TABLE IF NOT EXISTS `jxc_assembly_order` (
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
CREATE TABLE IF NOT EXISTS `jxc_assembly_order_detail` (
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
CREATE TABLE IF NOT EXISTS `jxc_stock_in_out` (
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
CREATE TABLE IF NOT EXISTS `jxc_stock_in_out_detail` (
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
CREATE TABLE IF NOT EXISTS `jxc_inventory_alert` (
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

-- -----------------------------------------------------------
-- 10. 库存流水表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_inventory_transaction` (
  `transaction_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `warehouse_id` BIGINT NOT NULL COMMENT '仓库ID',
  `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
  `trans_type` TINYINT NOT NULL COMMENT '类型：1-入库 2-出库 3-锁定 4-解锁 5-盘点调整',
  `qty` INT NOT NULL COMMENT '变动数量',
  `before_qty` INT DEFAULT NULL COMMENT '变动前库存',
  `after_qty` INT DEFAULT NULL COMMENT '变动后库存',
  `bill_type` VARCHAR(30) DEFAULT NULL COMMENT '源单据类型',
  `bill_id` BIGINT DEFAULT NULL COMMENT '源单据ID',
  `bill_no` VARCHAR(50) DEFAULT NULL COMMENT '源单据编号',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`transaction_id`),
  KEY `idx_warehouse_sku` (`warehouse_id`, `sku_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存流水表';

-- -----------------------------------------------------------
-- 11. 预订单发货记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_booking_delivery` (
  `delivery_id`       BIGINT NOT NULL AUTO_INCREMENT COMMENT '发货记录ID',
  `booking_order_id`  BIGINT NOT NULL COMMENT '预订单ID',
  `booking_detail_id` BIGINT NOT NULL COMMENT '预订单明细ID',
  `convert_qty`       INT NOT NULL COMMENT '本次转化数量',
  `sales_order_id`    BIGINT DEFAULT NULL COMMENT '关联的销售单ID（转单后填写）',
  `warehouse_id`      BIGINT DEFAULT NULL COMMENT '出库仓库ID',
  `operator_id`       BIGINT DEFAULT NULL COMMENT '操作人ID',
  `create_time`       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`         BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by`         BIGINT DEFAULT NULL COMMENT '更新人ID',
  `deleted`           INT DEFAULT 0 COMMENT '逻辑删除标记',
  PRIMARY KEY (`delivery_id`),
  KEY `idx_booking_order_id` (`booking_order_id`),
  KEY `idx_booking_detail_id` (`booking_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预订单发货记录表';

-- ======================== 财务模块 ========================

-- -----------------------------------------------------------
-- 12. 财务账户表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_finance_account` (
  `account_id`   BIGINT NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `account_name` VARCHAR(50) NOT NULL COMMENT '账户名称',
  `balance`      DECIMAL(10,2) DEFAULT 0.00 COMMENT '余额',
  `status`       TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-停用',
  `create_time`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`    BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by`    BIGINT DEFAULT NULL COMMENT '更新人ID',
  `deleted`      INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务账户表';

-- -----------------------------------------------------------
-- 13. 应收账款表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_receivable` (
  `receivable_id`   BIGINT NOT NULL AUTO_INCREMENT COMMENT '应收ID',
  `bill_no`         VARCHAR(64) NOT NULL COMMENT '单据编号',
  `customer_id`     BIGINT DEFAULT NULL COMMENT '客户ID',
  `customer_name`   VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
  `amount`          DECIMAL(18,2) DEFAULT NULL COMMENT '应收金额',
  `received_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '已收金额',
  `remaining_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '剩余金额',
  `status`          INT DEFAULT 0 COMMENT '状态 0-未核销 1-部分核销 2-已核销',
  `bill_date`       DATETIME DEFAULT NULL COMMENT '单据日期',
  `due_date`        DATETIME DEFAULT NULL COMMENT '到期日',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `source_type`     VARCHAR(30) DEFAULT NULL COMMENT '来源单据类型：SALES_ORDER/SALES_RETURN/PURCHASE_ORDER',
  `source_id`       BIGINT DEFAULT NULL COMMENT '来源单据ID',
  `source_doc_no`   VARCHAR(50) DEFAULT NULL COMMENT '来源单据编号',
  `created_by`      BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`receivable_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应收账款';

-- -----------------------------------------------------------
-- 14. 应收来源明细表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_receivable_source` (
  `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `receivable_id` BIGINT NOT NULL COMMENT '应收单ID',
  `order_id`      BIGINT DEFAULT NULL COMMENT '关联订单ID',
  `detail_id`     BIGINT DEFAULT NULL COMMENT '关联订单明细ID',
  `sku_id`        BIGINT DEFAULT NULL COMMENT '商品SKU ID',
  `qty`           INT DEFAULT NULL COMMENT '数量',
  `unit_price`    DECIMAL(10,4) DEFAULT NULL COMMENT '单价',
  `line_amount`   DECIMAL(12,2) DEFAULT NULL COMMENT '金额',
  `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`     BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by`     BIGINT DEFAULT NULL COMMENT '更新人ID',
  `deleted`       INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_receivable_id` (`receivable_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应收来源明细表';

-- -----------------------------------------------------------
-- 15. 应付账款表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_payable` (
  `payable_id`       BIGINT NOT NULL AUTO_INCREMENT COMMENT '应付ID',
  `bill_no`          VARCHAR(64) NOT NULL COMMENT '单据编号',
  `supplier_id`      BIGINT DEFAULT NULL COMMENT '供应商ID',
  `supplier_name`    VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
  `amount`           DECIMAL(18,2) DEFAULT NULL COMMENT '应付金额',
  `paid_amount`      DECIMAL(18,2) DEFAULT 0.00 COMMENT '已付金额',
  `remaining_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '剩余金额',
  `status`           INT DEFAULT 0 COMMENT '状态 0-未核销 1-部分核销 2-已核销',
  `bill_date`        DATETIME DEFAULT NULL COMMENT '单据日期',
  `due_date`         DATETIME DEFAULT NULL COMMENT '到期日',
  `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `source_type`      VARCHAR(30) DEFAULT NULL COMMENT '来源单据类型',
  `source_id`        BIGINT DEFAULT NULL COMMENT '来源单据ID',
  `source_doc_no`    VARCHAR(50) DEFAULT NULL COMMENT '来源单据编号',
  `created_by`       BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`          INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`payable_id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应付账款';

-- -----------------------------------------------------------
-- 16. 收款单表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_receipt` (
  `receipt_id`   BIGINT NOT NULL AUTO_INCREMENT COMMENT '收款单ID',
  `receipt_no`   VARCHAR(64) NOT NULL COMMENT '收款单号',
  `customer_id`  BIGINT DEFAULT NULL COMMENT '客户ID',
  `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
  `account_id`   BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name` VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `amount`       DECIMAL(18,2) DEFAULT NULL COMMENT '收款金额',
  `pay_method`   VARCHAR(50) DEFAULT NULL COMMENT '收款方式',
  `status`       INT DEFAULT 0 COMMENT '状态 0-待审核 1-已审核',
  `receipt_date` DATETIME DEFAULT NULL COMMENT '收款日期',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by`   BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`      INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`receipt_id`),
  UNIQUE KEY `uk_receipt_no` (`receipt_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收款单';

-- -----------------------------------------------------------
-- 17. 付款单表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_payment` (
  `payment_id`   BIGINT NOT NULL AUTO_INCREMENT COMMENT '付款单ID',
  `payment_no`   VARCHAR(64) NOT NULL COMMENT '付款单号',
  `supplier_id`  BIGINT DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
  `account_id`   BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name` VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `amount`       DECIMAL(18,2) DEFAULT NULL COMMENT '付款金额',
  `pay_method`   VARCHAR(50) DEFAULT NULL COMMENT '付款方式',
  `status`       INT DEFAULT 0 COMMENT '状态 0-待审核 1-已审核',
  `payment_date` DATETIME DEFAULT NULL COMMENT '付款日期',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by`   BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`      INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='付款单';

-- -----------------------------------------------------------
-- 18. 资金流水表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_finance_transaction` (
  `transaction_id`   BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `transaction_no`   VARCHAR(64) NOT NULL COMMENT '流水号',
  `account_id`       BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name`     VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `type`             INT DEFAULT NULL COMMENT '类型 1-收入 2-支出',
  `amount`           DECIMAL(18,2) DEFAULT NULL COMMENT '金额',
  `balance`          DECIMAL(18,2) DEFAULT NULL COMMENT '余额',
  `category`         VARCHAR(100) DEFAULT NULL COMMENT '收支分类',
  `bill_type`        VARCHAR(50) DEFAULT NULL COMMENT '关联单据类型',
  `bill_no`          VARCHAR(64) DEFAULT NULL COMMENT '关联单据编号',
  `remark`           VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `transaction_date` DATETIME DEFAULT NULL COMMENT '交易日期',
  `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`        BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by`        BIGINT DEFAULT NULL COMMENT '更新人ID',
  `deleted`          INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_account` (`account_id`),
  KEY `idx_type` (`type`),
  KEY `idx_transaction_date` (`transaction_date`),
  KEY `idx_bill_no` (`bill_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金流水';

-- -----------------------------------------------------------
-- 19. 核销单表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_write_off` (
  `write_off_id`        BIGINT NOT NULL AUTO_INCREMENT COMMENT '核销ID',
  `write_off_no`        VARCHAR(64) NOT NULL COMMENT '核销单号',
  `type`                VARCHAR(20) DEFAULT NULL COMMENT '核销类型 receivable-应收核销 payable-应付核销',
  `bill_no`             VARCHAR(64) DEFAULT NULL COMMENT '关联应收/应付单号',
  `bill_id`             BIGINT DEFAULT NULL COMMENT '关联应收/应付ID',
  `receipt_payment_id`  BIGINT DEFAULT NULL COMMENT '关联收款/付款单ID',
  `amount`              DECIMAL(18,2) DEFAULT NULL COMMENT '核销金额',
  `remark`              VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by`          BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time`         DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`             INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`write_off_id`),
  UNIQUE KEY `uk_write_off_no` (`write_off_no`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='核销单';

-- -----------------------------------------------------------
-- 20. 收支记录表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_income_expense` (
  `ie_id`         BIGINT NOT NULL AUTO_INCREMENT COMMENT '收支ID',
  `ie_no`         VARCHAR(64) NOT NULL COMMENT '收支单号',
  `type`          INT DEFAULT NULL COMMENT '类型 1-收入 2-支出',
  `category_id`   BIGINT DEFAULT NULL COMMENT '分类ID',
  `category_name` VARCHAR(100) DEFAULT NULL COMMENT '分类名称',
  `account_id`    BIGINT DEFAULT NULL COMMENT '账户ID',
  `account_name`  VARCHAR(100) DEFAULT NULL COMMENT '账户名称',
  `amount`        DECIMAL(18,2) DEFAULT NULL COMMENT '金额',
  `bill_type`     VARCHAR(50) DEFAULT NULL COMMENT '关联单据类型',
  `bill_no`       VARCHAR(64) DEFAULT NULL COMMENT '关联单据编号',
  `status`        INT DEFAULT 0 COMMENT '状态 0-待审核 1-已审核',
  `bill_date`     DATETIME DEFAULT NULL COMMENT '收支日期',
  `remark`        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by`    BIGINT DEFAULT NULL COMMENT '创建人ID',
  `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted`       INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`ie_id`),
  UNIQUE KEY `uk_ie_no` (`ie_no`),
  KEY `idx_type` (`type`),
  KEY `idx_category` (`category_id`),
  KEY `idx_account` (`account_id`),
  KEY `idx_bill_date` (`bill_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收支记录';

-- -----------------------------------------------------------
-- 21. 零售日结单表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_retail_settlement` (
  `settlement_id`  BIGINT NOT NULL AUTO_INCREMENT COMMENT '日结单ID',
  `settlement_no`  VARCHAR(50) NOT NULL COMMENT '日结单号 RS+yyyyMMdd+seq',
  `store_id`       BIGINT DEFAULT NULL COMMENT '门店ID',
  `settlement_date` DATE NOT NULL COMMENT '结算日期',
  `cash_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '现金收款',
  `wechat_amount`  DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '微信收款',
  `alipay_amount`  DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '支付宝收款',
  `card_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '银行卡收款',
  `other_amount`   DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '其他收款',
  `total_amount`   DECIMAL(12,2) NOT NULL COMMENT '合计收款',
  `order_count`    INT NOT NULL COMMENT '零售单数量',
  `operator_id`    BIGINT DEFAULT NULL COMMENT '操作人ID',
  `status`         TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-作废',
  `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`      BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by`      BIGINT DEFAULT NULL COMMENT '更新人ID',
  `deleted`        INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`settlement_id`),
  KEY `idx_settlement_date` (`settlement_date`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='零售日结单表';

-- -----------------------------------------------------------
-- 22. 日结单关联零售单明细表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `jxc_retail_settlement_order` (
  `id`                  BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `settlement_id`       BIGINT NOT NULL COMMENT '日结单ID',
  `sales_order_id`      BIGINT NOT NULL COMMENT '零售单ID',
  `sales_order_doc_no`  VARCHAR(50) DEFAULT NULL COMMENT '零售单号',
  `payment_method`      VARCHAR(20) DEFAULT NULL COMMENT 'CASH/WECHAT/ALIPAY/CARD/OTHER',
  `amount`              DECIMAL(12,2) NOT NULL COMMENT '金额',
  `create_time`         DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by`           BIGINT DEFAULT NULL COMMENT '创建人ID',
  `update_by`           BIGINT DEFAULT NULL COMMENT '更新人ID',
  `deleted`             INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_settlement_id` (`settlement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日结单关联零售单明细表';

-- ============================================================
-- 增量迁移：为存量表补充缺失字段（幂等 ADD COLUMN IF NOT EXISTS）
-- ============================================================

-- jxc_inventory 补充 available_qty / locked_qty
ALTER TABLE jxc_inventory ADD COLUMN IF NOT EXISTS `available_qty` INT NOT NULL DEFAULT 0 COMMENT '可用库存（总库存-已锁定）' AFTER `qty`;
ALTER TABLE jxc_inventory ADD COLUMN IF NOT EXISTS `locked_qty` INT NOT NULL DEFAULT 0 COMMENT '已锁定库存' AFTER `available_qty`;

-- jxc_sales_order_detail 补充 delivery_qty
ALTER TABLE jxc_sales_order_detail ADD COLUMN IF NOT EXISTS `delivery_qty` INT NOT NULL DEFAULT 0 COMMENT '已转发货数量（仅预订单有效）' AFTER `unfulfilled_qty`;

-- jxc_purchase_order 补充 discount_amount / other_fee / actual_amount / remark
ALTER TABLE jxc_purchase_order ADD COLUMN IF NOT EXISTS `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '折扣金额' AFTER `paid_amount`;
ALTER TABLE jxc_purchase_order ADD COLUMN IF NOT EXISTS `other_fee` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '其它费用' AFTER `discount_amount`;
ALTER TABLE jxc_purchase_order ADD COLUMN IF NOT EXISTS `actual_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '实际应付总计' AFTER `other_fee`;
ALTER TABLE jxc_purchase_order ADD COLUMN IF NOT EXISTS `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注' AFTER `actual_amount`;

-- jxc_purchase_order_detail 补充 deleted / create_by / update_by
ALTER TABLE jxc_purchase_order_detail ADD COLUMN IF NOT EXISTS `deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除' AFTER `warehouse_id`;
ALTER TABLE jxc_purchase_order_detail ADD COLUMN IF NOT EXISTS `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID' AFTER `deleted`;
ALTER TABLE jxc_purchase_order_detail ADD COLUMN IF NOT EXISTS `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID' AFTER `create_by`;

-- jxc_supplier 补充业务字段
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人' AFTER `supplier_name`;
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `contact_phone` VARCHAR(50) DEFAULT NULL COMMENT '联系电话' AFTER `contact_name`;
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `address` VARCHAR(255) DEFAULT NULL COMMENT '地址' AFTER `contact_phone`;
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `initial_arrears` DECIMAL(10,2) DEFAULT 0.00 COMMENT '初期应付' AFTER `address`;

-- ============================================================
-- 建表完成: 22张表 (库存模块11张 + 财务模块11张)
-- 增量迁移: 5组 ALTER TABLE
-- ============================================================
