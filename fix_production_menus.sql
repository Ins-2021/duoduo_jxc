INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, menu_type, path, perms, icon) VALUES 
(1100, 0, '生产管理', 'M', '/production', 'production:menu:view', 'Platform'),
(1200, 0, '生产执行', 'M', '/mes', 'mes:menu:view', 'Setting');

INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id) VALUES
(1, 1100), (1, 1200);
