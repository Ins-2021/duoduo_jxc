-- 修复菜单类型配置错误
-- 问题：部分菜单的 menu_type 被错误地设置为 'F'(按钮)，导致在前端侧边栏不显示
-- 修复：将生产订单、生产计划、工序管理、扫码计件的 menu_type 改为 'C'(菜单)

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 修复菜单类型：将 menu_type 从 'F'(按钮) 改为 'C'(菜单)
UPDATE jxc_sys_menu 
SET menu_type = 'C' 
WHERE menu_id IN (651, 652, 661, 662);

-- 验证修复结果
-- SELECT menu_id, parent_id, menu_name, menu_type, path 
-- FROM jxc_sys_menu 
-- WHERE menu_id IN (651, 652, 661, 662)
-- ORDER BY menu_id;