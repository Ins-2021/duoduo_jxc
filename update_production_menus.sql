INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, menu_type, path, perms) VALUES 
(1101, 1100, '生产订单', 'C', '/production/order', 'production:order:view'),
(1102, 1100, '生产计划', 'C', '/production/plan', 'production:plan:view'),
(1103, 1100, '生产排程', 'C', '/production/schedule', 'production:schedule:view'),
(1104, 1100, '裁床单', 'C', '/production/cut-order', 'production:cut-order:view'),
(1105, 1100, '裁床扎包', 'C', '/production/cut-bundle', 'production:cut-bundle:view'),
(1106, 1100, '进度看板', 'C', '/production/dashboard', 'production:dashboard:view'),
(1107, 1100, '质检记录', 'C', '/production/quality', 'production:quality:view'),
(1108, 1100, '返工记录', 'C', '/production/rework', 'production:rework:view'),

(1201, 1200, '工序管理', 'C', '/mes/process', 'mes:process:view'),
(1202, 1200, '扫码计件', 'C', '/mes/scan', 'mes:scan:view'),
(1203, 1200, '计件记录', 'C', '/mes/records', 'mes:records:view'),
(1204, 1200, '扎包流转', 'C', '/mes/bundle', 'mes:bundle:view'),
(1205, 1200, '巡检记录', 'C', '/mes/patrol', 'mes:patrol:view'),
(1206, 1200, '返工管理', 'C', '/mes/rework', 'mes:rework:view'),
(1207, 1200, 'AQL标准', 'C', '/mes/aql', 'mes:aql:view');

INSERT IGNORE INTO jxc_sys_role_menu (role_id, menu_id) VALUES
(1, 1101), (1, 1102), (1, 1103), (1, 1104), (1, 1105), (1, 1106), (1, 1107), (1, 1108),
(1, 1201), (1, 1202), (1, 1203), (1, 1204), (1, 1205), (1, 1206), (1, 1207);