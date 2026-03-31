-- 修复数据库UTF-8编码问题
-- 该脚本修复因字符集问题导致的乱码数据

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ============================================
-- 1. 修复商品表乱码
-- ============================================
UPDATE jxc_product_spu SET spu_name = '测试产品B' WHERE spu_id = 2;

-- ============================================
-- 2. 修复菜单表乱码（生产管理子菜单按钮）
-- ============================================
UPDATE jxc_sys_menu SET menu_name = '生产订单新增' WHERE menu_id = 6511;
UPDATE jxc_sys_menu SET menu_name = '生产订单编辑' WHERE menu_id = 6512;
UPDATE jxc_sys_menu SET menu_name = '生产订单删除' WHERE menu_id = 6513;

-- ============================================
-- 3. 检查并修复其他可能存在的乱码数据
-- ============================================

-- 修复商品分类名称（如果存在乱码）
-- UPDATE jxc_product_category SET category_name = '正确的名称' WHERE category_name = '乱码';

-- 修复客户名称（如果存在乱码）
-- UPDATE jxc_customer SET customer_name = '正确的名称' WHERE customer_name = '乱码';

-- 修复供应商名称（如果存在乱码）
-- UPDATE jxc_supplier SET supplier_name = '正确的名称' WHERE supplier_name = '乱码';

-- ============================================
-- 4. 验证修复结果
-- ============================================
-- SELECT '修复后的商品' as check_item, spu_id, spu_name FROM jxc_product_spu WHERE spu_id = 2;
-- SELECT '修复后的菜单' as check_item, menu_id, menu_name FROM jxc_sys_menu WHERE menu_id IN (6511, 6512, 6513);