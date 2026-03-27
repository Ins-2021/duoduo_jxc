ALTER TABLE `jxc_product_sku` DROP INDEX `idx_sku_code`;
ALTER TABLE `jxc_product_sku` ADD INDEX `idx_sku_code` (`sku_code`);

ALTER TABLE `jxc_product_sku` ADD COLUMN `warning_qty` int(11) DEFAULT '0' COMMENT '库存预警值' AFTER `weight`;
