-- =============================================
-- 第四轮复测修复 - 数据库列修复 V2026.03.31
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

CALL safe_add_column('jxc_product_spu', 'spec', 'VARCHAR(500) DEFAULT NULL COMMENT "规格型号" AFTER spu_name');
CALL safe_add_column('jxc_customer', 'level', 'VARCHAR(50) DEFAULT NULL COMMENT "客户等级" AFTER address');
CALL safe_add_column('jxc_size_category', 'code', 'VARCHAR(50) DEFAULT NULL COMMENT "尺码编码" AFTER category_name');
CALL safe_add_column('jxc_write_off', 'created_by', 'BIGINT DEFAULT NULL COMMENT "创建人ID"');

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (830, 800, '裁床单管理', 14, '/data/cut-order', 'DataCutOrder', 'Scissors', 'C', 1, 1, 'data:production:cut-order:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8301, 830, '裁床单新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:production:cut-order:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8302, 830, '裁床单编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:production:cut-order:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (8303, 830, '裁床单删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:production:cut-order:delete', 0);

INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM jxc_sys_menu WHERE perms IN (
    'data:production:cut-order:view', 'data:production:cut-order:add',
    'data:production:cut-order:edit', 'data:production:cut-order:delete'
);

DROP PROCEDURE IF EXISTS safe_add_column;
