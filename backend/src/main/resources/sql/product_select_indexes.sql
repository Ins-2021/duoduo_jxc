-- 商品选择器查询性能优化索引
-- 为 ProductSelectMapper 的多表 JOIN 查询添加组合索引

-- 商品 SKU 表：主查询条件索引
CREATE INDEX IF NOT EXISTS idx_sku_deleted ON jxc_product_sku(deleted);
CREATE INDEX IF NOT EXISTS idx_sku_spu_id ON jxc_product_sku(spu_id);

-- 商品 SPU 表：JOIN 条件和搜索条件
CREATE INDEX IF NOT EXISTS idx_spu_deleted ON jxc_product_spu(deleted);
CREATE INDEX IF NOT EXISTS idx_spu_category_id ON jxc_product_spu(category_id);

-- 分类表：JOIN 条件
CREATE INDEX IF NOT EXISTS idx_category_deleted ON jxc_product_category(deleted);

-- 品牌表：JOIN 条件
CREATE INDEX IF NOT EXISTS idx_brand_deleted ON jxc_product_brand(deleted);

-- 库存表：LEFT JOIN 条件
CREATE INDEX IF NOT EXISTS idx_inventory_sku_id ON jxc_inventory(sku_id);

-- SPU 表名称搜索（LIKE 前缀搜索优化）
CREATE INDEX IF NOT EXISTS idx_spu_name ON jxc_product_spu(spu_name(50));

-- SKU 条码搜索
CREATE INDEX IF NOT EXISTS idx_sku_code ON jxc_product_sku(sku_code(50));

-- SKU 属性搜索
CREATE INDEX IF NOT EXISTS idx_sku_attr1 ON jxc_product_sku(attr1(30));
CREATE INDEX IF NOT EXISTS idx_sku_attr2 ON jxc_product_sku(attr2(30));
