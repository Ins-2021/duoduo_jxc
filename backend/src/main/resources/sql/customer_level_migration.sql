SET @has_level := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'jxc_customer'
    AND COLUMN_NAME = 'level'
);

SET @sql_add_level := IF(
  @has_level = 0,
  'ALTER TABLE `jxc_customer` ADD COLUMN `level` VARCHAR(20) DEFAULT ''normal'' COMMENT ''客户等级 (normal:普通客户, vip:VIP客户)'' AFTER `address`',
  'SELECT 1'
);

PREPARE stmt FROM @sql_add_level;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
