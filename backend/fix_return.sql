USE duoduo_jxc;
UPDATE jxc_sys_menu SET menu_name = '新增退货单' WHERE perms = 'sales:return:add';
UPDATE jxc_sys_menu SET menu_name = '修改' WHERE perms = 'sales:return:edit';
UPDATE jxc_sys_menu SET menu_name = '审核' WHERE perms = 'sales:return:audit';
UPDATE jxc_sys_menu SET menu_name = '删除' WHERE perms = 'sales:return:delete';
