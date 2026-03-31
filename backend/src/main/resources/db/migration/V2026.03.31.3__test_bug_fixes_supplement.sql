-- =============================================
-- 补充修复：菜单权限缺失 (Section 10.1 补充)
-- 由于 INSERT IGNORE 按 menu_id 主键去重，811/812/813 等已存在导致跳过
-- 使用新的 menu_id 重新插入
-- =============================================

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10500, 800, 'BOM管理', 8, '/data/bom', 'DataBom', 'Document', 'C', 1, 1, 'data:bom:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10501, 10500, 'BOM新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:bom:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10502, 10500, 'BOM编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:bom:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10503, 10500, 'BOM删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:bom:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10510, 800, '款式管理', 9, '/data/style', 'DataStyle', 'Brush', 'C', 1, 1, 'data:style:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10511, 10510, '款式新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:style:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10512, 10510, '款式编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:style:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10513, 10510, '款式删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:style:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10520, 800, '颜色管理', 10, '/data/color', 'DataColor', 'Palette', 'C', 1, 1, 'data:color:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10521, 10520, '颜色新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:color:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10522, 10520, '颜色编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:color:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10523, 10520, '颜色删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:color:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10530, 800, '尺码分类', 11, '/data/size-category', 'DataSizeCategory', 'Scale', 'C', 1, 1, 'data:sizecategory:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10531, 10530, '尺码新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:sizecategory:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10532, 10530, '尺码编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:sizecategory:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10533, 10530, '尺码删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:sizecategory:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10540, 800, '工序管理', 12, '/data/process', 'DataProcess', 'Tools', 'C', 1, 1, 'data:process:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10541, 10540, '工序新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:process:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10542, 10540, '工序编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:process:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10543, 10540, '工序删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:process:delete', 0);

INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10550, 800, '扎包管理', 13, '/data/bundle', 'DataBundle', 'Box', 'C', 1, 1, 'data:bundle:view', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10551, 10550, '扎包新增', 1, NULL, NULL, NULL, 'F', 1, 1, 'data:bundle:add', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10552, 10550, '扎包编辑', 2, NULL, NULL, NULL, 'F', 1, 1, 'data:bundle:edit', 0);
INSERT IGNORE INTO jxc_sys_menu (menu_id, parent_id, menu_name, order_num, path, route_name, icon, menu_type, visible, status, perms, deleted)
VALUES (10553, 10550, '扎包删除', 3, NULL, NULL, NULL, 'F', 1, 1, 'data:bundle:delete', 0);

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
