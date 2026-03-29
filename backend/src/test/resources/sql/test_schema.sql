-- ============================================================
-- 条码管理模块 DDL
-- 日期: 2026-03-29
-- ============================================================

-- 条码生成规则表
CREATE TABLE IF NOT EXISTS `jxc_barcode_rule` (
  `rule_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_type` VARCHAR(20) DEFAULT NULL COMMENT '类型: SKU/BOX/BATCH',
  `prefix` VARCHAR(20) DEFAULT NULL COMMENT '前缀',
  `date_format` VARCHAR(20) DEFAULT NULL COMMENT '日期格式, 如 yyyyMMdd',
  `seq_length` INT DEFAULT 4 COMMENT '序列号长度',
  `rule_expression` VARCHAR(200) DEFAULT NULL COMMENT '规则表达式, 如 {prefix}{date}{seq}',
  `example` VARCHAR(100) DEFAULT NULL COMMENT '示例',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认规则 0-否 1-是',
  `is_active` TINYINT DEFAULT 1 COMMENT '是否启用 0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`rule_id`),
  KEY `idx_rule_type` (`rule_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='条码生成规则表';

-- 条码记录表
CREATE TABLE IF NOT EXISTS `jxc_barcode` (
  `barcode_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '条码ID',
  `barcode_content` VARCHAR(100) NOT NULL COMMENT '条码内容',
  `ref_type` VARCHAR(20) DEFAULT NULL COMMENT '关联类型: SKU/BATCH/BOX',
  `ref_id` BIGINT DEFAULT NULL COMMENT '关联ID',
  `rule_id` BIGINT DEFAULT NULL COMMENT '生成规则ID',
  `barcode_type` VARCHAR(20) DEFAULT NULL COMMENT '条码类型: SKU/BOX/BATCH',
  `printed` TINYINT DEFAULT 0 COMMENT '是否已打印 0-否 1-是',
  `print_time` DATETIME DEFAULT NULL COMMENT '打印时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`barcode_id`),
  UNIQUE KEY `uk_barcode_content` (`barcode_content`),
  KEY `idx_ref` (`ref_type`, `ref_id`),
  KEY `idx_barcode_type` (`barcode_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='条码记录表';
-- ============================================================
-- 批次管理模块 DDL
-- 日期: 2026-03-29
-- ============================================================

-- 批次管理表
CREATE TABLE IF NOT EXISTS `jxc_batch` (
  `batch_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_no` VARCHAR(64) NOT NULL COMMENT '批次号',
  `sku_id` BIGINT DEFAULT NULL COMMENT 'SKU ID',
  `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
  `production_date` DATE DEFAULT NULL COMMENT '生产日期',
  `expiry_date` DATE DEFAULT NULL COMMENT '保质期/到期日',
  `inbound_date` DATE DEFAULT NULL COMMENT '入库日期',
  `qty` INT DEFAULT NULL COMMENT '批次数量',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`batch_id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='批次管理';

-- jxc_inventory 增加 batch_no 字段
ALTER TABLE jxc_inventory ADD COLUMN IF NOT EXISTS `batch_no` VARCHAR(64) DEFAULT NULL COMMENT '批次号' AFTER `locked_qty`;
-- ============================================================
-- 单号规则表
-- ============================================================

CREATE TABLE IF NOT EXISTS `jxc_doc_no_rule` (
  `rule_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `doc_type` VARCHAR(32) NOT NULL COMMENT '单据类型',
  `doc_name` VARCHAR(50) NOT NULL COMMENT '单据名称',
  `prefix` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '编号前缀',
  `template` VARCHAR(50) DEFAULT NULL COMMENT '规则模板',
  `seq_length` INT DEFAULT 4 COMMENT '流水号位数',
  `include_year` INT DEFAULT 1 COMMENT '是否包含年份',
  `include_month` INT DEFAULT 1 COMMENT '是否包含月份',
  `include_day` INT DEFAULT 1 COMMENT '是否包含日期',
  `use_random` INT DEFAULT 0 COMMENT '是否启用随机数',
  `random_length` INT DEFAULT 6 COMMENT '随机数长度',
  `status` INT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`rule_id`),
  UNIQUE KEY `uk_doc_type` (`doc_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单号规则';

-- ============================================================
-- 初始化21种单据类型的编号规则
-- ============================================================

INSERT IGNORE INTO `jxc_doc_no_rule` (`doc_type`, `doc_name`, `prefix`, `template`, `seq_length`, `include_year`, `include_month`, `include_day`, `use_random`, `random_length`, `status`) VALUES
('SALES_ORDER', '销售单', 'XS', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('SALES_BOOKING', '销售预定', 'YD', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('SALES_RETURN', '销售退货', 'XT', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('SALES_RETAIL', '零售单', 'LS', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('PURCHASE_ORDER', '进货单', 'JH', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('PURCHASE_RETURN', '进货退货', 'HT', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('TRANSFER', '调拨单', 'DB', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('INVENTORY_CHECK', '盘点单', 'PD', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('ASSEMBLY', '组装拆卸', 'ZZ', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('STOCK_IN', '其他入库', 'RK', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('STOCK_OUT', '其他出库', 'CK', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('RECEIVABLE', '应收单', 'YS', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('PAYABLE', '应付单', 'YF', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('RECEIPT', '收款单', 'SK', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('PAYMENT', '付款单', 'FK', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('TRANSACTION', '资金流水', 'ZJ', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('WRITE_OFF', '核销单', 'HX', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('INCOME_EXPENSE', '收支记录', 'SZ', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('PRODUCT_SPU', '商品', 'SP', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('CUSTOMER', '客户', 'KH', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1),
('SUPPLIER', '供应商', 'GYS', '{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1);
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
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
  id VARCHAR(100) NOT NULL,
  client_id VARCHAR(100) NOT NULL,
  client_id_issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  client_secret VARCHAR(200) DEFAULT NULL,
  client_secret_expires_at TIMESTAMP DEFAULT NULL,
  client_name VARCHAR(200) NOT NULL,
  client_authentication_methods VARCHAR(1000) NOT NULL,
  authorization_grant_types VARCHAR(1000) NOT NULL,
  redirect_uris VARCHAR(1000) DEFAULT NULL,
  post_logout_redirect_uris VARCHAR(1000) DEFAULT NULL,
  scopes VARCHAR(1000) NOT NULL,
  client_settings VARCHAR(2000) NOT NULL,
  token_settings VARCHAR(2000) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS oauth2_authorization (
  id VARCHAR(100) NOT NULL,
  registered_client_id VARCHAR(100) NOT NULL,
  principal_name VARCHAR(200) NOT NULL,
  authorization_grant_type VARCHAR(100) NOT NULL,
  authorized_scopes VARCHAR(1000) DEFAULT NULL,
  attributes BLOB DEFAULT NULL,
  state VARCHAR(500) DEFAULT NULL,
  authorization_code_value BLOB DEFAULT NULL,
  authorization_code_issued_at TIMESTAMP DEFAULT NULL,
  authorization_code_expires_at TIMESTAMP DEFAULT NULL,
  authorization_code_metadata BLOB DEFAULT NULL,
  access_token_value BLOB DEFAULT NULL,
  access_token_issued_at TIMESTAMP DEFAULT NULL,
  access_token_expires_at TIMESTAMP DEFAULT NULL,
  access_token_metadata BLOB DEFAULT NULL,
  access_token_type VARCHAR(100) DEFAULT NULL,
  access_token_scopes VARCHAR(1000) DEFAULT NULL,
  oidc_id_token_value BLOB DEFAULT NULL,
  oidc_id_token_issued_at TIMESTAMP DEFAULT NULL,
  oidc_id_token_expires_at TIMESTAMP DEFAULT NULL,
  oidc_id_token_metadata BLOB DEFAULT NULL,
  refresh_token_value BLOB DEFAULT NULL,
  refresh_token_issued_at TIMESTAMP DEFAULT NULL,
  refresh_token_expires_at TIMESTAMP DEFAULT NULL,
  refresh_token_metadata BLOB DEFAULT NULL,
  user_code_value BLOB DEFAULT NULL,
  user_code_issued_at TIMESTAMP DEFAULT NULL,
  user_code_expires_at TIMESTAMP DEFAULT NULL,
  user_code_metadata BLOB DEFAULT NULL,
  device_code_value BLOB DEFAULT NULL,
  device_code_issued_at TIMESTAMP DEFAULT NULL,
  device_code_expires_at TIMESTAMP DEFAULT NULL,
  device_code_metadata BLOB DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_oauth2_auth_registered_client_id (registered_client_id),
  KEY idx_oauth2_auth_principal_name (principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
  registered_client_id VARCHAR(100) NOT NULL,
  principal_name VARCHAR(200) NOT NULL,
  authorities VARCHAR(1000) NOT NULL,
  PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_sys_user (
  user_id BIGINT NOT NULL AUTO_INCREMENT,
  dept_id BIGINT NULL COMMENT '部门ID',
  username VARCHAR(64) NOT NULL COMMENT '账号',
  real_name VARCHAR(64) NULL COMMENT '姓名',
  password VARCHAR(255) NULL COMMENT '密码(加密)',
  store_id BIGINT NULL COMMENT '门店ID',
  store_name VARCHAR(128) NULL COMMENT '门店名称',
  remark VARCHAR(255) NULL COMMENT '备注',
  permissions TEXT NULL COMMENT '兼容字段(逗号分隔权限)',
  data_auth TEXT NULL COMMENT '数据权限JSON/预留',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_username (username),
  KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS jxc_sys_role (
  role_id BIGINT NOT NULL AUTO_INCREMENT,
  role_key VARCHAR(64) NOT NULL COMMENT '角色标识',
  role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
  data_scope CHAR(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  remark VARCHAR(255) NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (role_id),
  UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS jxc_sys_dept (
  dept_id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
  ancestors VARCHAR(255) NULL COMMENT '祖级列表',
  dept_name VARCHAR(64) NOT NULL COMMENT '部门名称',
  order_num INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  leader VARCHAR(64) NULL COMMENT '负责人',
  phone VARCHAR(20) NULL COMMENT '联系电话',
  email VARCHAR(64) NULL COMMENT '邮箱',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (dept_id),
  KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS jxc_sys_post (
  post_id BIGINT NOT NULL AUTO_INCREMENT,
  post_code VARCHAR(64) NOT NULL COMMENT '岗位编码',
  post_name VARCHAR(64) NOT NULL COMMENT '岗位名称',
  post_sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (post_id),
  UNIQUE KEY uk_post_code (post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

CREATE TABLE IF NOT EXISTS jxc_sys_user_post (
  user_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

CREATE TABLE IF NOT EXISTS jxc_sys_role_dept (
  role_id BIGINT NOT NULL,
  dept_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色部门关联表';

CREATE TABLE IF NOT EXISTS jxc_sys_menu (
  menu_id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
  order_num INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  path VARCHAR(255) NULL COMMENT '路由地址',
  component VARCHAR(255) NULL COMMENT '组件路径',
  route_name VARCHAR(128) NULL COMMENT '路由名称',
  icon VARCHAR(64) NULL COMMENT '菜单图标',
  menu_type CHAR(1) NOT NULL DEFAULT 'M' COMMENT '类型(M目录 C菜单 F按钮)',
  visible TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见(0否1是)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  perms VARCHAR(128) NULL COMMENT '权限标识',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (menu_id),
  KEY idx_menu_parent (parent_id),
  KEY idx_menu_type (menu_type),
  KEY idx_menu_perms (perms)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE IF NOT EXISTS jxc_sys_user_role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  create_time DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS jxc_sys_role_menu (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  create_time DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_menu (role_id, menu_id),
  KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
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
-- ============================================================
-- 供应商评级和对账模块 DDL
-- 日期: 2026-03-29
-- ============================================================

-- jxc_supplier 增加评级和对账相关字段
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `rating` VARCHAR(5) DEFAULT NULL COMMENT '评级: A/B/C/D' AFTER `initial_arrears`;
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账户' AFTER `rating`;
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `bank_name` VARCHAR(100) DEFAULT NULL COMMENT '开户行' AFTER `bank_account`;
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `settlement_type` VARCHAR(30) DEFAULT NULL COMMENT '结算方式' AFTER `bank_name`;
ALTER TABLE jxc_supplier ADD COLUMN IF NOT EXISTS `credit_limit` DECIMAL(12,2) DEFAULT NULL COMMENT '信用额度' AFTER `settlement_type`;

-- 供应商对账单表
CREATE TABLE IF NOT EXISTS `jxc_supplier_reconciliation` (
  `reconciliation_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '对账单ID',
  `recon_no` VARCHAR(64) NOT NULL COMMENT '对账单号',
  `supplier_id` BIGINT DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
  `start_date` DATE DEFAULT NULL COMMENT '对账开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '对账结束日期',
  `total_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '应付金额',
  `paid_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '已付金额',
  `unpaid_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '未付金额',
  `status` INT DEFAULT 0 COMMENT '对账状态 0-未对账 1-已对账 2-已确认',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`reconciliation_id`),
  UNIQUE KEY `uk_recon_no` (`recon_no`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商对账单';
CREATE TABLE IF NOT EXISTS jxc_wf_model (
  id BIGINT NOT NULL AUTO_INCREMENT,
  model_key VARCHAR(128) NOT NULL,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(128) NULL,
  bpmn_xml LONGTEXT NOT NULL,
  version INT NOT NULL DEFAULT 1,
  status VARCHAR(32) NOT NULL DEFAULT 'draft',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_model_key (model_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_model_publish (
  id BIGINT NOT NULL AUTO_INCREMENT,
  model_id BIGINT NOT NULL,
  deployment_id VARCHAR(64) NOT NULL,
  process_def_id VARCHAR(64) NOT NULL,
  process_def_key VARCHAR(128) NOT NULL,
  process_def_version INT NOT NULL,
  published_by BIGINT NULL,
  published_at DATETIME NULL,
  remark VARCHAR(255) NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_wf_publish_model_id (model_id),
  KEY idx_wf_publish_def_key_version (process_def_key, process_def_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_biz_process_binding (
  id BIGINT NOT NULL AUTO_INCREMENT,
  biz_type VARCHAR(64) NOT NULL,
  process_def_key VARCHAR(128) NOT NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  start_condition TEXT NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_binding_biz_type (biz_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_instance (
  id BIGINT NOT NULL AUTO_INCREMENT,
  proc_inst_id VARCHAR(64) NOT NULL,
  business_key VARCHAR(128) NOT NULL,
  biz_type VARCHAR(64) NOT NULL,
  biz_id VARCHAR(64) NOT NULL,
  title VARCHAR(255) NULL,
  initiator_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'running',
  start_time DATETIME NULL,
  end_time DATETIME NULL,
  current_task_names VARCHAR(512) NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_instance_proc_inst_id (proc_inst_id),
  KEY idx_wf_instance_biz (biz_type, biz_id),
  KEY idx_wf_instance_initiator (initiator_id),
  KEY idx_wf_instance_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_task (
  id BIGINT NOT NULL AUTO_INCREMENT,
  task_id VARCHAR(64) NOT NULL,
  proc_inst_id VARCHAR(64) NOT NULL,
  biz_type VARCHAR(64) NOT NULL,
  biz_id VARCHAR(64) NOT NULL,
  node_key VARCHAR(128) NOT NULL,
  node_name VARCHAR(255) NOT NULL,
  assignee_id BIGINT NULL,
  candidate_summary VARCHAR(512) NULL,
  due_time DATETIME NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'todo',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_task_task_id (task_id),
  KEY idx_wf_task_proc_inst (proc_inst_id),
  KEY idx_wf_task_biz (biz_type, biz_id),
  KEY idx_wf_task_assignee_status (assignee_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_task_action_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  proc_inst_id VARCHAR(64) NOT NULL,
  task_id VARCHAR(64) NOT NULL,
  action VARCHAR(32) NOT NULL,
  operator_id BIGINT NOT NULL,
  comment TEXT NULL,
  attachments_json TEXT NULL,
  created_at DATETIME NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_wf_action_proc_inst (proc_inst_id),
  KEY idx_wf_action_task_id (task_id),
  KEY idx_wf_action_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS product_code VARCHAR(100) DEFAULT NULL COMMENT '商品编号' AFTER spu_name;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS spec VARCHAR(255) DEFAULT NULL COMMENT '规格型号' AFTER product_code;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS image_urls VARCHAR(1000) DEFAULT NULL COMMENT '商品图像' AFTER spec;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS purchase_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '进货价格' AFTER brand_id;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS retail_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '零售价格' AFTER purchase_price;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS wholesale_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '批发价格' AFTER retail_price;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS exchange_points INT DEFAULT 0 COMMENT '兑换积分' AFTER wholesale_price;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS default_warehouse_id BIGINT DEFAULT NULL COMMENT '默认仓库' AFTER exchange_points;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS product_location VARCHAR(100) DEFAULT NULL COMMENT '商品货位' AFTER default_warehouse_id;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS warning_qty INT DEFAULT 0 COMMENT '库存预警' AFTER product_location;
ALTER TABLE jxc_product_spu ADD COLUMN IF NOT EXISTS remark VARCHAR(500) DEFAULT NULL COMMENT '备注信息' AFTER warning_qty;
SET @has_level := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'jxc_customer'
    AND COLUMN_NAME = 'level'
);

SET @sql_add_level := IF(
  @has_level = 0,
  'ALTER TABLE `jxc_customer` ADD COLUMN `level` VARCHAR(20) DEFAULT ''normal'' COMMENT ''客户等级 (normal:普通客户, vip:VIP客户)'' AFTER `address`',
  'SELECT 1'
);

PREPARE stmt FROM @sql_add_level;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
-- RBAC schema migration (idempotent via continue-on-error)
-- 1) sys_user add dept_id
ALTER TABLE jxc_sys_user
  ADD COLUMN dept_id BIGINT NULL COMMENT '部门ID' AFTER user_id;

ALTER TABLE jxc_sys_user
  ADD INDEX idx_dept_id (dept_id);

-- 2) sys_role add data_scope
ALTER TABLE jxc_sys_role
  ADD COLUMN data_scope CHAR(1) DEFAULT '1' COMMENT '数据范围（1：全部 2：自定义 3：本部门 4：本部门及以下 5：仅本人）' AFTER role_name;

-- 3) dept/post tables
CREATE TABLE IF NOT EXISTS jxc_sys_dept (
  dept_id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
  ancestors VARCHAR(255) NULL COMMENT '祖级列表',
  dept_name VARCHAR(64) NOT NULL COMMENT '部门名称',
  order_num INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  leader VARCHAR(64) NULL COMMENT '负责人',
  phone VARCHAR(20) NULL COMMENT '联系电话',
  email VARCHAR(64) NULL COMMENT '邮箱',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (dept_id),
  KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS jxc_sys_post (
  post_id BIGINT NOT NULL AUTO_INCREMENT,
  post_code VARCHAR(64) NOT NULL COMMENT '岗位编码',
  post_name VARCHAR(64) NOT NULL COMMENT '岗位名称',
  post_sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (post_id),
  UNIQUE KEY uk_post_code (post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

CREATE TABLE IF NOT EXISTS jxc_sys_user_post (
  user_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

CREATE TABLE IF NOT EXISTS jxc_sys_role_dept (
  role_id BIGINT NOT NULL,
  dept_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色部门关联表';

-- 商品选择器查询性能优化索引
-- 为 ProductSelectMapper 的多表 JOIN 查询添加组合索引

-- 商品 SKU 表：主查询条件索引
CREATE INDEX IF NOT EXISTS idx_sku_deleted ON jxc_product_sku(deleted);
CREATE INDEX IF NOT EXISTS idx_sku_spu_id ON jxc_product_sku(spu_id);

-- 商品 SPU 表：JOIN 条件和搜索条件
CREATE INDEX IF NOT EXISTS idx_spu_deleted ON jxc_product_spu(deleted);
CREATE INDEX IF NOT EXISTS idx_spu_category_id ON jxc_product_spu(category_id);

-- 分类表：JOIN 条件
CREATE INDEX IF NOT EXISTS idx_category_deleted ON jxc_product_category(deleted);

-- 品牌表：JOIN 条件
CREATE INDEX IF NOT EXISTS idx_brand_deleted ON jxc_product_brand(deleted);

-- 库存表：LEFT JOIN 条件
CREATE INDEX IF NOT EXISTS idx_inventory_sku_id ON jxc_inventory(sku_id);

-- SPU 表名称搜索（LIKE 前缀搜索优化）
CREATE INDEX IF NOT EXISTS idx_spu_name ON jxc_product_spu(spu_name(50));

-- SKU 条码搜索
CREATE INDEX IF NOT EXISTS idx_sku_code ON jxc_product_sku(sku_code(50));

-- SKU 属性搜索
CREATE INDEX IF NOT EXISTS idx_sku_attr1 ON jxc_product_sku(attr1(30));
CREATE INDEX IF NOT EXISTS idx_sku_attr2 ON jxc_product_sku(attr2(30));
