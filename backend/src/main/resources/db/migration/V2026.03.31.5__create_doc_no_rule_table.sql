-- 创建单号规则表
CREATE TABLE IF NOT EXISTS jxc_doc_no_rule (
    rule_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    doc_type VARCHAR(50) NOT NULL COMMENT '单据类型',
    doc_name VARCHAR(100) NOT NULL COMMENT '单据名称',
    prefix VARCHAR(20) DEFAULT NULL COMMENT '编号前缀',
    template VARCHAR(200) DEFAULT NULL COMMENT '规则模板（如：XS{Y}{M}{D}{###}）',
    seq_length INT DEFAULT 4 COMMENT '流水号位数',
    include_year TINYINT DEFAULT 1 COMMENT '是否包含年份（1-是 0-否）',
    include_month TINYINT DEFAULT 1 COMMENT '是否包含月份（1-是 0-否）',
    include_day TINYINT DEFAULT 0 COMMENT '是否包含日期（1-是 0-否）',
    use_random TINYINT DEFAULT 0 COMMENT '是否启用随机数（1-是 0-否）',
    random_length INT DEFAULT 0 COMMENT '随机数长度',
    status TINYINT DEFAULT 1 COMMENT '状态（1-启用 0-禁用）',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (rule_id),
    UNIQUE KEY uk_doc_type (doc_type),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='单号规则表';

-- 插入默认单号规则数据
INSERT INTO jxc_doc_no_rule (doc_type, doc_name, prefix, template, seq_length, include_year, include_month, include_day, use_random, random_length, status, remark) VALUES
('XS', '销售订单', 'XS', 'XS{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '销售订单单号规则'),
('XSTH', '销售退货单', 'XSTH', 'XSTH{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '销售退货单单号规则'),
('CG', '采购订单', 'CG', 'CG{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '采购订单单号规则'),
('CGTH', '采购退货单', 'CGTH', 'CGTH{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '采购退货单单号规则'),
('RK', '入库单', 'RK', 'RK{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '入库单单号规则'),
('CK', '出库单', 'CK', 'CK{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '出库单单号规则'),
('PD', '盘点单', 'PD', 'PD{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '盘点单单号规则'),
('DB', '调拨单', 'DB', 'DB{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '调拨单单号规则'),
('SK', '收款单', 'SK', 'SK{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '收款单单号规则'),
('FK', '付款单', 'FK', 'FK{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '付款单单号规则'),
('SCRW', '生产任务单', 'SCRW', 'SCRW{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '生产任务单单号规则'),
('ZJ', '质检单', 'ZJ', 'ZJ{Y}{M}{D}{####}', 4, 1, 1, 1, 0, 0, 1, '质检单单号规则')
ON DUPLICATE KEY UPDATE update_time = CURRENT_TIMESTAMP;