-- 添加生产管理和生产执行（MES）菜单
-- 修复服装生产流程菜单缺失问题
-- 设置字符集为UTF-8
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 生产管理主菜单
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (650, 0, '生产管理', 5, '/production', NULL, 'Production', 'Platform', 'M', 1, 1, 'production:menu:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产管理',
    path = '/production',
    icon = 'Platform',
    visible = 1,
    status = 1,
    perms = 'production:menu:view',
    update_time = NOW();

-- 生产订单
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (651, 650, '生产订单', 1, '/production/order', NULL, 'ProductionOrder', 'List', 'C', 1, 1, 'production:order:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产订单',
    path = '/production/order',
    visible = 1,
    status = 1,
    perms = 'production:order:view',
    update_time = NOW();

-- 生产订单新增按钮
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (6511, 651, '生产订单新增', 1, NULL, NULL, NULL, NULL, 'F', 0, 1, 'production:order:add', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产订单新增',
    visible = 0,
    status = 1,
    perms = 'production:order:add',
    update_time = NOW();

-- 生产订单编辑按钮
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (6512, 651, '生产订单编辑', 2, NULL, NULL, NULL, NULL, 'F', 0, 1, 'production:order:edit', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产订单编辑',
    visible = 0,
    status = 1,
    perms = 'production:order:edit',
    update_time = NOW();

-- 生产订单删除按钮
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (6513, 651, '生产订单删除', 3, NULL, NULL, NULL, NULL, 'F', 0, 1, 'production:order:delete', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产订单删除',
    visible = 0,
    status = 1,
    perms = 'production:order:delete',
    update_time = NOW();

-- 生产计划
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (652, 650, '生产计划', 2, '/production/plan', NULL, 'ProductionPlan', 'Calendar', 'C', 1, 1, 'production:plan:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产计划',
    path = '/production/plan',
    visible = 1,
    status = 1,
    perms = 'production:plan:view',
    update_time = NOW();

-- 生产排程
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (653, 650, '生产排程', 3, '/production/schedule', NULL, 'ProductionSchedule', 'Timer', 'C', 1, 1, 'production:schedule:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产排程',
    path = '/production/schedule',
    visible = 1,
    status = 1,
    perms = 'production:schedule:view',
    update_time = NOW();

-- 裁床单
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (654, 650, '裁床单', 4, '/production/cut-order', NULL, 'CutOrder', 'Scissors', 'C', 1, 1, 'production:cut-order:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '裁床单',
    path = '/production/cut-order',
    visible = 1,
    status = 1,
    perms = 'production:cut-order:view',
    update_time = NOW();

-- 裁床扎包
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (655, 650, '裁床扎包', 5, '/production/cut-bundle', NULL, 'CutBundle', 'Box', 'C', 1, 1, 'production:cut-bundle:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '裁床扎包',
    path = '/production/cut-bundle',
    visible = 1,
    status = 1,
    perms = 'production:cut-bundle:view',
    update_time = NOW();

-- 进度看板
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (656, 650, '进度看板', 6, '/production/dashboard', NULL, 'ProductionDashboard', 'Dashboard', 'C', 1, 1, 'production:dashboard:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '进度看板',
    path = '/production/dashboard',
    visible = 1,
    status = 1,
    perms = 'production:dashboard:view',
    update_time = NOW();

-- 质检记录
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (657, 650, '质检记录', 7, '/production/quality', NULL, 'ProductionQuality', 'Checked', 'C', 1, 1, 'production:quality:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '质检记录',
    path = '/production/quality',
    visible = 1,
    status = 1,
    perms = 'production:quality:view',
    update_time = NOW();

-- 返工记录
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (658, 650, '返工记录', 8, '/production/rework', NULL, 'ProductionRework', 'RefreshLeft', 'C', 1, 1, 'production:rework:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '返工记录',
    path = '/production/rework',
    visible = 1,
    status = 1,
    perms = 'production:rework:view',
    update_time = NOW();

-- 生产执行（MES）主菜单
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (660, 0, '生产执行', 6, '/mes', NULL, 'MES', 'Setting', 'M', 1, 1, 'mes:menu:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '生产执行',
    path = '/mes',
    icon = 'Setting',
    visible = 1,
    status = 1,
    perms = 'mes:menu:view',
    update_time = NOW();

-- 工序管理
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (661, 660, '工序管理', 1, '/mes/process', NULL, 'MesProcess', 'Operation', 'C', 1, 1, 'mes:process:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '工序管理',
    path = '/mes/process',
    visible = 1,
    status = 1,
    perms = 'mes:process:view',
    update_time = NOW();

-- 扫码计件
INSERT INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, component, route_name, icon, menu_type, visible, status, perms, create_time, update_time, create_by, update_by, deleted) 
VALUES (662, 660, '扫码计件', 2, '/mes/scan', NULL, 'MesScan', 'Scan', 'C', 1, 1, 'mes:scan:view', NOW(), NOW(), 1, 1, 0)
ON DUPLICATE KEY UPDATE 
    menu_name = '扫码计件',
    path = '/mes/scan',
    visible = 1,
    status = 1,
    perms = 'mes:scan:view',
    update_time = NOW();

-- 给管理员角色分配新菜单权限
INSERT INTO jxc_sys_role_menu (role_id, menu_id) 
SELECT 1, menu_id FROM jxc_sys_menu 
WHERE menu_id IN (650, 651, 6511, 6512, 6513, 652, 653, 654, 655, 656, 657, 658, 660, 661, 662)
ON DUPLICATE KEY UPDATE role_id = role_id;