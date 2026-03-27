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
