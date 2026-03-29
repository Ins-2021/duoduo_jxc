INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id) 
SELECT 1, menu_id FROM jxc_sys_menu WHERE perms IS NOT NULL;
