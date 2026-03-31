-- 删除设置模块中已废弃的权限管理和数据授权菜单
-- 这两个功能已在系统管理中实现，设置中不再需要

-- 删除权限设置菜单（menu_id = 903）
DELETE FROM jxc_sys_menu WHERE menu_id = 903;

-- 删除数据授权菜单（menu_id = 904）
DELETE FROM jxc_sys_menu WHERE menu_id = 904;

-- 清理相关的权限数据（如果存在）
DELETE FROM jxc_sys_role_menu WHERE menu_id IN (903, 904);

-- 注意：如果有专门的数据表存储权限设置和数据授权，也需要删除
-- 但目前这两个模块只是前端界面，没有专门的后端数据表