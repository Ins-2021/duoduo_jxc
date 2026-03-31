-- =============================================
-- 测试报告Bug修复迁移脚本 V2026.03.31
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

CALL safe_add_column('jxc_receivable', 'create_by', 'BIGINT DEFAULT NULL COMMENT "创建人ID"');
CALL safe_add_column('jxc_receivable', 'update_by', 'BIGINT DEFAULT NULL COMMENT "更新人ID"');
CALL safe_add_column('jxc_receivable', 'deleted', 'TINYINT DEFAULT 0 COMMENT "逻辑删除"');

CREATE TABLE IF NOT EXISTS jxc_first_article_confirmation (
    confirmation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    confirmation_no VARCHAR(32) DEFAULT NULL COMMENT '确认编号',
    order_id BIGINT DEFAULT NULL COMMENT '订单ID',
    process_id BIGINT DEFAULT NULL COMMENT '工序ID',
    status VARCHAR(20) DEFAULT NULL COMMENT '状态',
    result VARCHAR(20) DEFAULT NULL COMMENT '结果',
    checker_id BIGINT DEFAULT NULL COMMENT '检验人ID',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID',
    approve_comment VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    approve_time DATETIME DEFAULT NULL COMMENT '审批时间',
    check_time DATETIME DEFAULT NULL COMMENT '检验时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='首件确认';

CREATE TABLE IF NOT EXISTS jxc_defect_record (
    defect_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    defect_no VARCHAR(32) DEFAULT NULL COMMENT '缺陷编号',
    quality_check_id BIGINT DEFAULT NULL COMMENT '质检ID',
    rework_id BIGINT DEFAULT NULL COMMENT '返工ID',
    defect_type VARCHAR(50) DEFAULT NULL COMMENT '缺陷类型',
    quantity INT DEFAULT NULL COMMENT '数量',
    handling_method VARCHAR(50) DEFAULT NULL COMMENT '处理方式',
    handling_remark VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
    handled_by BIGINT DEFAULT NULL COMMENT '处理人ID',
    handled_at DATETIME DEFAULT NULL COMMENT '处理时间',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缺陷记录';

CREATE TABLE IF NOT EXISTS jxc_process_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_no VARCHAR(32) DEFAULT NULL COMMENT '记录编号',
    bundle_id BIGINT DEFAULT NULL COMMENT '扎包ID',
    process_id BIGINT DEFAULT NULL COMMENT '工序ID',
    sku_id BIGINT DEFAULT NULL COMMENT 'SKU ID',
    worker_id BIGINT DEFAULT NULL COMMENT '主工人ID',
    is_team_work INT DEFAULT 0 COMMENT '是否团队协作',
    team_type VARCHAR(20) DEFAULT NULL COMMENT '协作类型',
    team_group_id VARCHAR(50) DEFAULT NULL COMMENT '团队组ID',
    quantity INT DEFAULT NULL COMMENT '计件数量',
    price_level VARCHAR(10) DEFAULT NULL COMMENT '价格等级',
    base_price DECIMAL(15,4) DEFAULT NULL COMMENT 'SPU基准单价',
    premium_ratio DECIMAL(8,4) DEFAULT NULL COMMENT 'SKU溢价比例',
    unit_price DECIMAL(15,4) DEFAULT NULL COMMENT '最终单价',
    share_ratio DECIMAL(8,4) DEFAULT NULL COMMENT '分配比例',
    amount DECIMAL(15,4) DEFAULT NULL COMMENT '计件金额',
    record_type VARCHAR(20) DEFAULT 'normal' COMMENT '记录类型',
    related_rework_id BIGINT DEFAULT NULL COMMENT '关联返工单ID',
    deduction_reason VARCHAR(500) DEFAULT NULL COMMENT '扣款原因',
    settlement_status VARCHAR(20) DEFAULT 'unsettled' COMMENT '结算状态',
    salary_sheet_id BIGINT DEFAULT NULL COMMENT '关联工资单ID',
    settled_at DATETIME DEFAULT NULL COMMENT '结算时间',
    scan_time DATETIME DEFAULT NULL COMMENT '扫码时间',
    confirm_time DATETIME DEFAULT NULL COMMENT '确认时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
    device_id VARCHAR(50) DEFAULT NULL COMMENT '设备ID',
    location VARCHAR(100) DEFAULT NULL COMMENT '位置',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_bundle_id (bundle_id),
    INDEX idx_worker_id (worker_id),
    INDEX idx_settlement_status (settlement_status),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工序计件记录';

CREATE TABLE IF NOT EXISTS jxc_capacity_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_type VARCHAR(50) DEFAULT NULL COMMENT '预警类型',
    workshop_id BIGINT DEFAULT NULL COMMENT '车间ID',
    process_id BIGINT DEFAULT NULL COMMENT '工序ID',
    alert_level VARCHAR(20) DEFAULT NULL COMMENT '预警级别',
    threshold_value DECIMAL(15,4) DEFAULT NULL COMMENT '阈值',
    current_value DECIMAL(15,4) DEFAULT NULL COMMENT '当前值',
    message VARCHAR(500) DEFAULT NULL COMMENT '预警消息',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    handled_by BIGINT DEFAULT NULL COMMENT '处理人',
    handled_at DATETIME DEFAULT NULL COMMENT '处理时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_workshop_id (workshop_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产能预警';

CREATE TABLE IF NOT EXISTS jxc_capacity_alert_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(100) DEFAULT NULL COMMENT '规则名称',
    alert_type VARCHAR(50) DEFAULT NULL COMMENT '预警类型',
    workshop_id BIGINT DEFAULT NULL COMMENT '车间ID',
    process_id BIGINT DEFAULT NULL COMMENT '工序ID',
    metric_type VARCHAR(50) DEFAULT NULL COMMENT '指标类型',
    condition_type VARCHAR(20) DEFAULT NULL COMMENT '条件类型',
    threshold_value DECIMAL(15,4) DEFAULT NULL COMMENT '阈值',
    alert_level VARCHAR(20) DEFAULT NULL COMMENT '预警级别',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产能预警规则';

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (347, 304, '零售日结', 8, NULL, NULL, NULL, 'F', 1, 1, 'retail:settlement:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (3471, 347, '日结单生成', 1, NULL, NULL, NULL, 'F', 1, 1, 'retail:settlement:create', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (710, 700, '资金账户管理', 1, '/finance/account', 'FinanceAccount', 'Wallet', 'C', 1, 1, 'finance:account:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (7101, 710, '账户新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'finance:account:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (7102, 710, '账户编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'finance:account:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (7103, 710, '账户删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'finance:account:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (950, 900, '成本报表', 5, '/report/cost', 'ReportCost', 'Coin', 'C', 1, 1, 'cost:report:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (9501, 950, '成本报表导出', 1, NULL, NULL, NULL, 'F', 1, 1, 'cost:report:export', 0);

INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM jxc_sys_menu WHERE perms IN (
    'retail:settlement:view', 'retail:settlement:create',
    'finance:account:view', 'finance:account:add', 'finance:account:edit', 'finance:account:delete',
    'cost:report:view', 'cost:report:export'
);

CALL safe_add_column('jxc_sales_order_detail', 'delivery_qty', 'INT DEFAULT 0 COMMENT "已发货数量" AFTER unfulfilled_qty');

CREATE TABLE IF NOT EXISTS jxc_quality_check (
    check_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_no VARCHAR(32) NOT NULL UNIQUE COMMENT '质检编号',
    bundle_id BIGINT DEFAULT NULL COMMENT '扎包ID',
    order_id BIGINT DEFAULT NULL COMMENT '订单ID',
    process_id BIGINT DEFAULT NULL COMMENT '工序ID',
    check_type VARCHAR(20) DEFAULT NULL COMMENT '质检类型',
    check_quantity INT DEFAULT 0 COMMENT '检查数量',
    qualified_quantity INT DEFAULT 0 COMMENT '合格数量',
    defective_quantity INT DEFAULT 0 COMMENT '不合格数量',
    check_result VARCHAR(20) DEFAULT NULL COMMENT '质检结果',
    check_time DATETIME DEFAULT NULL COMMENT '质检时间',
    inspector_id BIGINT DEFAULT NULL COMMENT '质检员ID',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_check_no (check_no),
    INDEX idx_bundle_id (bundle_id),
    INDEX idx_check_time (check_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='质检记录表';

CREATE TABLE IF NOT EXISTS jxc_style (
    style_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    style_no VARCHAR(32) NOT NULL UNIQUE COMMENT '款式编号',
    style_name VARCHAR(100) NOT NULL COMMENT '款式名称',
    year INT DEFAULT NULL COMMENT '年份',
    season VARCHAR(20) DEFAULT NULL COMMENT '季节',
    series VARCHAR(50) DEFAULT NULL COMMENT '系列',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    brand_id BIGINT DEFAULT NULL COMMENT '品牌ID',
    style_type VARCHAR(20) DEFAULT NULL COMMENT '款式类型',
    target_gender VARCHAR(10) DEFAULT NULL COMMENT '目标性别',
    design_image VARCHAR(500) DEFAULT NULL COMMENT '设计图',
    sample_image VARCHAR(500) DEFAULT NULL COMMENT '样衣图',
    status INT DEFAULT 1 COMMENT '状态',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_style_no (style_no),
    INDEX idx_style_name (style_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='款式表';

CREATE TABLE IF NOT EXISTS jxc_color (
    color_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '颜色名称',
    code VARCHAR(20) NOT NULL UNIQUE COMMENT '颜色编码',
    color_value VARCHAR(20) DEFAULT NULL COMMENT '颜色值',
    pantone_no VARCHAR(30) DEFAULT NULL COMMENT '潘通号',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active INT DEFAULT 1 COMMENT '是否启用',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='颜色表';

CREATE TABLE IF NOT EXISTS jxc_size_category (
    size_category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    size_codes TEXT DEFAULT NULL COMMENT '尺码编码列表',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active INT DEFAULT 1 COMMENT '是否启用',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='尺码分类表';

CREATE TABLE IF NOT EXISTS jxc_process (
    process_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_no VARCHAR(32) NOT NULL UNIQUE COMMENT '工序编号',
    process_name VARCHAR(100) NOT NULL COMMENT '工序名称',
    category VARCHAR(20) DEFAULT NULL COMMENT '工序大类',
    skill_level VARCHAR(20) DEFAULT NULL COMMENT '技能等级要求',
    is_active INT DEFAULT 1 COMMENT '是否启用',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_process_no (process_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工序表';

CREATE TABLE IF NOT EXISTS jxc_bundle (
    bundle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bundle_no VARCHAR(32) NOT NULL UNIQUE COMMENT '扎包号',
    cutting_bundle_id BIGINT DEFAULT NULL COMMENT '裁床扎包ID',
    order_id BIGINT DEFAULT NULL COMMENT '订单ID',
    size VARCHAR(20) DEFAULT NULL COMMENT '尺码',
    color VARCHAR(50) DEFAULT NULL COMMENT '颜色',
    quantity INT DEFAULT 0 COMMENT '数量',
    current_process_id BIGINT DEFAULT NULL COMMENT '当前工序ID',
    work_group_id BIGINT DEFAULT NULL COMMENT '负责班组ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
    qr_code VARCHAR(500) DEFAULT NULL COMMENT '二维码',
    location VARCHAR(100) DEFAULT NULL COMMENT '当前位置',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_bundle_no (bundle_no),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='扎包表';

CREATE TABLE IF NOT EXISTS jxc_bom (
    bom_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bom_no VARCHAR(32) NOT NULL UNIQUE COMMENT 'BOM编号',
    style_id BIGINT NOT NULL COMMENT '款式ID',
    version VARCHAR(20) DEFAULT '1.0' COMMENT '版本',
    status INT DEFAULT 1 COMMENT '状态',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_style_id (style_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BOM主表';

CREATE TABLE IF NOT EXISTS jxc_bom_process (
    bom_process_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bom_id BIGINT NOT NULL COMMENT 'BOM ID',
    process_id BIGINT NOT NULL COMMENT '工序ID',
    sequence INT DEFAULT 0 COMMENT '工序顺序',
    standard_minutes INT DEFAULT NULL COMMENT '标准工时(分钟)',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_bom_id (bom_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BOM工序表';

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (674, 600, '收支审批', 5, NULL, NULL, NULL, 'F', 1, 1, 'finance:income-expense:approve', 0);

INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM jxc_sys_menu WHERE perms = 'finance:income-expense:approve';

CALL safe_add_column('jxc_write_off', 'created_by', 'BIGINT DEFAULT NULL COMMENT "创建人"');

CREATE TABLE IF NOT EXISTS jxc_wf_biz_process_binding (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    biz_type VARCHAR(50) NOT NULL COMMENT '业务类型',
    process_def_key VARCHAR(100) DEFAULT NULL COMMENT '流程定义Key',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    start_condition VARCHAR(500) DEFAULT NULL COMMENT '启动条件',
    create_by BIGINT DEFAULT NULL,
    update_by BIGINT DEFAULT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_biz_type (biz_type),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流业务绑定表';

CALL safe_add_column('jxc_product_spu', 'product_code', 'VARCHAR(50) DEFAULT NULL COMMENT "商品编码" AFTER spu_id');
CALL safe_add_column('jxc_product_sku', 'product_code', 'VARCHAR(50) DEFAULT NULL COMMENT "商品编码" AFTER sku_id');

INSERT IGNORE INTO jxc_wf_biz_process_binding (biz_type, process_def_key, enabled, start_condition, deleted)
VALUES ('SALES_ORDER', NULL, 0, NULL, 0);

DROP PROCEDURE IF EXISTS safe_add_column;

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (811, 800, 'BOM管理', 8, '/data/bom', 'DataBom', 'Document', 'C', 1, 1, 'data:bom:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8111, 811, 'BOM新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:bom:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8112, 811, 'BOM编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:bom:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8113, 811, 'BOM删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:bom:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (820, 800, '生产订单', 9, '/data/production-order', 'DataProductionOrder', 'List', 'C', 1, 1, 'data:production:order:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8201, 820, '生产订单新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:production:order:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8202, 820, '生产订单编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:production:order:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8203, 820, '生产订单删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:production:order:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (812, 800, '款式管理', 9, '/data/style', 'DataStyle', 'Brush', 'C', 1, 1, 'data:style:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8121, 812, '款式新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:style:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8122, 812, '款式编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:style:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8123, 812, '款式删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:style:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (813, 800, '颜色管理', 10, '/data/color', 'DataColor', 'Palette', 'C', 1, 1, 'data:color:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8131, 813, '颜色新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:color:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8132, 813, '颜色编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:color:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8133, 813, '颜色删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:color:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (814, 800, '尺码分类', 11, '/data/size-category', 'DataSizeCategory', 'Scale', 'C', 1, 1, 'data:sizecategory:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8141, 814, '尺码新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:sizecategory:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8142, 814, '尺码编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:sizecategory:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8143, 814, '尺码删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:sizecategory:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (815, 800, '工序管理', 12, '/data/process', 'DataProcess', 'Tools', 'C', 1, 1, 'data:process:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8151, 815, '工序新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:process:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8152, 815, '工序编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:process:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8153, 815, '工序删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:process:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (816, 800, '扎包管理', 13, '/data/bundle', 'DataBundle', 'Box', 'C', 1, 1, 'data:bundle:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8161, 816, '扎包新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:bundle:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8162, 816, '扎包编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:bundle:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8163, 816, '扎包删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:bundle:delete', 0);

INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM jxc_sys_menu WHERE perms IN (
    'data:bom:view', 'data:bom:add', 'data:bom:edit', 'data:bom:delete',
    'data:production:order:view', 'data:production:order:add', 'data:production:order:edit', 'data:production:order:delete',
    'data:style:view', 'data:style:add', 'data:style:edit', 'data:style:delete',
    'data:color:view', 'data:color:add', 'data:color:edit', 'data:color:delete',
    'data:sizecategory:view', 'data:sizecategory:add', 'data:sizecategory:edit', 'data:sizecategory:delete',
    'data:process:view', 'data:process:add', 'data:process:edit', 'data:process:delete',
    'data:bundle:view', 'data:bundle:add', 'data:bundle:edit', 'data:bundle:delete'
);
