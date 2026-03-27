-- 财务流水表
CREATE TABLE IF NOT EXISTS jxc_finance_transaction (
    transaction_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    transaction_no VARCHAR(50) NOT NULL COMMENT '流水号',
    account_id BIGINT COMMENT '账户ID',
    account_name VARCHAR(100) COMMENT '账户名称',
    type TINYINT NOT NULL COMMENT '类型 1-收入 2-支出 3-转账',
    amount DECIMAL(12,2) NOT NULL COMMENT '金额',
    balance DECIMAL(12,2) COMMENT '余额',
    category VARCHAR(30) COMMENT '类别',
    bill_type VARCHAR(30) COMMENT '单据类型',
    bill_no VARCHAR(50) COMMENT '单据编号',
    remark VARCHAR(500) COMMENT '备注',
    transaction_date DATETIME NOT NULL COMMENT '交易日期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    PRIMARY KEY (transaction_id),
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_account_id (account_id),
    KEY idx_transaction_date (transaction_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务流水表';
