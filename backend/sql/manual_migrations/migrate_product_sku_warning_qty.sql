SET @has_warning_qty := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'jxc_product_sku'
    AND COLUMN_NAME = 'warning_qty'
);
SET @sql_add_col := IF(
  @has_warning_qty = 0,
  'ALTER TABLE `jxc_product_sku` ADD COLUMN `warning_qty` int(11) DEFAULT 0 COMMENT ''库存预警值'' AFTER `weight`',
  'SELECT 1'
);
PREPARE stmt FROM @sql_add_col;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @is_unique := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'jxc_product_sku'
    AND INDEX_NAME = 'idx_sku_code'
    AND NON_UNIQUE = 0
);
SET @sql_drop_idx := IF(
  @is_unique > 0,
  'ALTER TABLE `jxc_product_sku` DROP INDEX `idx_sku_code`',
  'SELECT 1'
);
PREPARE stmt2 FROM @sql_drop_idx;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

SET @has_idx := (
  SELECT COUNT(*)
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'jxc_product_sku'
    AND INDEX_NAME = 'idx_sku_code'
);
SET @sql_add_idx := IF(
  @has_idx = 0,
  'ALTER TABLE `jxc_product_sku` ADD INDEX `idx_sku_code` (`sku_code`)',
  'SELECT 1'
);
PREPARE stmt3 FROM @sql_add_idx;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

