-- =============================================
-- 第八轮复测修复：补齐工序组、盘点、调拨相关表结构
-- =============================================

DELIMITER //

DROP PROCEDURE IF EXISTS safe_add_column//

CREATE PROCEDURE safe_add_column(
    IN tbl VARCHAR(100),
    IN col VARCHAR(100),
    IN col_def VARCHAR(500)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = tbl AND COLUMN_NAME = col
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', tbl, '` ADD COLUMN `', col, '` ', col_def);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

DELIMITER ;

CREATE TABLE IF NOT EXISTS jxc_work_group (
    group_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(30) NOT NULL,
    factory_id BIGINT DEFAULT NULL,
    workshop_id BIGINT DEFAULT NULL,
    leader_id BIGINT DEFAULT NULL,
    group_type VARCHAR(30) DEFAULT NULL,
    is_active TINYINT DEFAULT 1,
    remark TEXT,
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_code (code),
    KEY idx_factory_id (factory_id),
    KEY idx_workshop_id (workshop_id),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班组表';

CREATE TABLE IF NOT EXISTS jxc_inventory_check (
    check_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '盘点单ID',
    check_no VARCHAR(64) NOT NULL COMMENT '盘点单号',
    warehouse_id BIGINT DEFAULT NULL COMMENT '仓库ID',
    warehouse_name VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
    status INT DEFAULT 0 COMMENT '状态',
    check_date DATETIME DEFAULT NULL COMMENT '盘点时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_check_no (check_no),
    KEY idx_warehouse_id (warehouse_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存盘点单';

CREATE TABLE IF NOT EXISTS jxc_inventory_check_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_id BIGINT NOT NULL,
    sku_id BIGINT DEFAULT NULL,
    sku_code VARCHAR(64) DEFAULT NULL,
    sku_name VARCHAR(200) DEFAULT NULL,
    attr1 VARCHAR(50) DEFAULT NULL,
    attr2 VARCHAR(50) DEFAULT NULL,
    system_qty INT DEFAULT 0,
    actual_qty INT DEFAULT 0,
    diff_qty INT DEFAULT 0,
    cost_price DECIMAL(10,2) DEFAULT 0.00,
    diff_amount DECIMAL(10,2) DEFAULT 0.00,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_check_id (check_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存盘点单明细';

CREATE TABLE IF NOT EXISTS jxc_transfer_order (
    transfer_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '调拨单ID',
    transfer_no VARCHAR(64) NOT NULL COMMENT '调拨单号',
    from_warehouse_id BIGINT DEFAULT NULL COMMENT '调出仓库ID',
    from_warehouse_name VARCHAR(100) DEFAULT NULL COMMENT '调出仓库名称',
    to_warehouse_id BIGINT DEFAULT NULL COMMENT '调入仓库ID',
    to_warehouse_name VARCHAR(100) DEFAULT NULL COMMENT '调入仓库名称',
    status INT DEFAULT 0 COMMENT '状态',
    transfer_date DATETIME DEFAULT NULL COMMENT '调拨时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_transfer_no (transfer_no),
    KEY idx_from_warehouse_id (from_warehouse_id),
    KEY idx_to_warehouse_id (to_warehouse_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存调拨单';

CREATE TABLE IF NOT EXISTS jxc_transfer_order_detail (
    detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transfer_id BIGINT NOT NULL,
    sku_id BIGINT DEFAULT NULL,
    sku_code VARCHAR(64) DEFAULT NULL,
    sku_name VARCHAR(200) DEFAULT NULL,
    attr1 VARCHAR(50) DEFAULT NULL,
    attr2 VARCHAR(50) DEFAULT NULL,
    qty INT DEFAULT 0,
    cost_price DECIMAL(10,2) DEFAULT 0.00,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_transfer_id (transfer_id),
    KEY idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存调拨单明细';

CALL safe_add_column('jxc_inventory_check', 'create_by', 'BIGINT DEFAULT NULL COMMENT "创建人ID"');
CALL safe_add_column('jxc_inventory_check', 'update_by', 'BIGINT DEFAULT NULL COMMENT "更新人ID"');
CALL safe_add_column('jxc_transfer_order', 'create_by', 'BIGINT DEFAULT NULL COMMENT "创建人ID"');
CALL safe_add_column('jxc_transfer_order', 'update_by', 'BIGINT DEFAULT NULL COMMENT "更新人ID"');

DROP PROCEDURE IF EXISTS safe_add_column;
