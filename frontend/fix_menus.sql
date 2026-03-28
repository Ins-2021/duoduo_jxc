USE duoduo_jxc;

-- Update sales booking names
UPDATE jxc_sys_menu SET menu_name = '新增预定单' WHERE perms = 'sales:booking:add';
UPDATE jxc_sys_menu SET menu_name = '修改' WHERE perms = 'sales:booking:edit';
UPDATE jxc_sys_menu SET menu_name = '审核' WHERE perms = 'sales:booking:audit';
UPDATE jxc_sys_menu SET menu_name = '删除' WHERE perms = 'sales:booking:delete';
UPDATE jxc_sys_menu SET menu_name = '转销售' WHERE perms = 'sales:booking:convert';

-- Insert missing ones if not exist
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, menu_type, visible, status, perms, deleted)
VALUES 
(319, 301, '转销售', 5, 'F', 1, 1, 'sales:booking:convert', 0),
(320, 301, '发货', 6, 'F', 1, 1, 'sales:booking:delivery', 0);

-- Update sales order names
UPDATE jxc_sys_menu SET menu_name = '新增销售单' WHERE perms = 'sales:order:add';
UPDATE jxc_sys_menu SET menu_name = '修改' WHERE perms = 'sales:order:edit';
UPDATE jxc_sys_menu SET menu_name = '审核' WHERE perms = 'sales:order:audit';
UPDATE jxc_sys_menu SET menu_name = '删除' WHERE perms = 'sales:order:delete';
UPDATE jxc_sys_menu SET menu_name = '转销售预定' WHERE perms = 'sales:order:convert';

-- Insert missing for sales order
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, menu_type, visible, status, perms, deleted)
VALUES 
(325, 302, '转销售预定', 5, 'F', 1, 1, 'sales:order:convert', 0);

-- Make sure admin has all permissions
INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM jxc_sys_menu WHERE deleted = 0;
