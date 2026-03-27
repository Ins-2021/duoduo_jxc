CREATE DATABASE IF NOT EXISTS `duoduo_jxc` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `duoduo_jxc`;
SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for jxc_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `jxc_sys_config`;
CREATE TABLE `jxc_sys_config` (
  `config_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL,
  `config_value` varchar(2000) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- Table structure for jxc_product_category
-- ----------------------------
DROP TABLE IF EXISTS `jxc_product_category`;
CREATE TABLE `jxc_product_category` (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID(0为根)',
  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`category_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- Table structure for jxc_product_brand
-- ----------------------------
DROP TABLE IF EXISTS `jxc_product_brand`;
CREATE TABLE `jxc_product_brand` (
  `brand_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brand_name` varchar(100) NOT NULL COMMENT '品牌名称',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`brand_id`),
  UNIQUE KEY `uk_brand_name` (`brand_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品品牌表';

-- ----------------------------
-- Table structure for jxc_product_spu
-- ----------------------------
DROP TABLE IF EXISTS `jxc_product_spu`;
CREATE TABLE `jxc_product_spu` (
  `spu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'SPU ID',
  `spu_name` varchar(255) NOT NULL COMMENT 'SPU 名称',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类 ID',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌 ID',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:下架, 1:上架)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记 (0:未删除, 1:已删除)',
  PRIMARY KEY (`spu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品 SPU 表';

-- ----------------------------
-- Table structure for jxc_product_sku
-- ----------------------------
DROP TABLE IF EXISTS `jxc_product_sku`;
CREATE TABLE `jxc_product_sku` (
  `sku_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `spu_id` bigint(20) NOT NULL COMMENT '关联的 SPU ID',
  `sku_code` varchar(100) NOT NULL COMMENT 'SKU 编码/条形码',
  `attr1` varchar(50) DEFAULT NULL COMMENT '属性 1 (如：颜色-黑色)',
  `attr2` varchar(50) DEFAULT NULL COMMENT '属性 2 (如：尺码-XL)',
  `purchase_price` decimal(10,2) DEFAULT '0.00' COMMENT '进货价',
  `retail_price` decimal(10,2) DEFAULT '0.00' COMMENT '零售价',
  `wholesale_price` decimal(10,2) DEFAULT '0.00' COMMENT '批发价',
  `weight` decimal(10,2) DEFAULT '0.00' COMMENT '重量',
  `warning_qty` int(11) DEFAULT '0' COMMENT '库存预警值',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`sku_id`),
  KEY `idx_sku_code` (`sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品 SKU 表';

-- ----------------------------
-- Table structure for jxc_file_resource
-- ----------------------------
DROP TABLE IF EXISTS `jxc_file_resource`;
CREATE TABLE `jxc_file_resource` (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL COMMENT '原始文件名(安全化后)',
  `content_type` varchar(128) DEFAULT NULL COMMENT 'MIME',
  `size` bigint(20) DEFAULT NULL COMMENT '字节大小',
  `storage_path` varchar(512) NOT NULL COMMENT '存储相对路径',
  `sha256` varchar(64) DEFAULT NULL COMMENT 'SHA256',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`file_id`),
  KEY `idx_sha256` (`sha256`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件资源表';

-- ----------------------------
-- Table structure for jxc_sales_order
-- ----------------------------
DROP TABLE IF EXISTS `jxc_sales_order`;
CREATE TABLE `jxc_sales_order` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
  `doc_no` varchar(50) NOT NULL COMMENT '单据编号',
  `order_type` tinyint(2) NOT NULL COMMENT '业务类型 (1:批发单, 2:零售单, 3:销售预订单)',
  `doc_date` date NOT NULL COMMENT '业务日期',
  `store_id` bigint(20) DEFAULT NULL COMMENT '所属门店 ID',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户 ID',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '制单人 ID',
  `status` tinyint(2) DEFAULT '10' COMMENT '状态 (10:草稿, 20:待审核, 30:执行中/部分发货, 40:已完成, 90:已作废)',
  `settle_status` tinyint(1) DEFAULT '0' COMMENT '财务核销状态 (0:未核销, 1:部分核销, 2:已核销)',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '折前总额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '整体折扣金额',
  `other_fee` decimal(10,2) DEFAULT '0.00' COMMENT '其它费用',
  `actual_amount` decimal(10,2) DEFAULT '0.00' COMMENT '实际应收总计',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '已收金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `audit_by` bigint(20) DEFAULT NULL COMMENT '审核人 ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `idx_doc_no` (`doc_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

-- ----------------------------
-- Table structure for jxc_sales_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `jxc_sales_order_detail`;
CREATE TABLE `jxc_sales_order_detail` (
  `detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细 ID',
  `order_id` bigint(20) NOT NULL COMMENT '所属销售单 ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `spu_id` bigint(20) NOT NULL COMMENT 'SPU ID',
  `qty` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
  `booked_qty` int(11) DEFAULT '0' COMMENT '预定数量',
  `unfulfilled_qty` int(11) DEFAULT '0' COMMENT '未转销售数量',
  `unit_price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '单行折扣',
  `line_amount` decimal(10,2) DEFAULT '0.00' COMMENT '单行总金额',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '库房 ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`detail_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单明细表';

-- ----------------------------
-- Table structure for jxc_ui_view_config
-- ----------------------------
DROP TABLE IF EXISTS `jxc_ui_view_config`;
CREATE TABLE `jxc_ui_view_config` (
  `config_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `view_key` varchar(120) NOT NULL COMMENT '页面/视图标识',
  `view_name` varchar(200) NOT NULL COMMENT '页面/视图名称',
  `scene` varchar(30) NOT NULL COMMENT '场景(list/form/export/print)',
  `config_json` longtext NOT NULL COMMENT '当前配置JSON',
  `default_json` longtext NOT NULL COMMENT '默认配置JSON',
  `version` int(11) DEFAULT '1' COMMENT '版本号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标记',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_view_scene` (`view_key`, `scene`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前端视图字段/列配置';

INSERT INTO `jxc_ui_view_config` (`view_key`, `view_name`, `scene`, `config_json`, `default_json`, `version`, `remark`)
VALUES
('sales_order_list_by_order', '销售单-按单据', 'list',
 '[{\"prop\":\"selection\",\"originalLabel\":\"\",\"label\":\"\",\"visible\":true,\"type\":\"selection\",\"width\":55,\"fixed\":false},{\"prop\":\"storeId\",\"originalLabel\":\"所属门店\",\"label\":\"所属门店\",\"visible\":true,\"width\":100},{\"prop\":\"docDate\",\"originalLabel\":\"单据日期\",\"label\":\"单据日期\",\"visible\":true,\"width\":120},{\"prop\":\"docNo\",\"originalLabel\":\"单据编号\",\"label\":\"单据编号\",\"visible\":true,\"width\":180},{\"prop\":\"customerId\",\"originalLabel\":\"客户\",\"label\":\"客户\",\"visible\":true,\"width\":120},{\"prop\":\"totalAmount\",\"originalLabel\":\"总计金额(元)\",\"label\":\"总计金额(元)\",\"visible\":true,\"width\":120},{\"prop\":\"discountAmount\",\"originalLabel\":\"折扣金额(元)\",\"label\":\"折扣金额(元)\",\"visible\":true,\"width\":120},{\"prop\":\"actualAmount\",\"originalLabel\":\"折后金额(元)\",\"label\":\"折后金额(元)\",\"visible\":true,\"width\":120},{\"prop\":\"paidAmount\",\"originalLabel\":\"已收金额(元)\",\"label\":\"已收金额(元)\",\"visible\":true,\"width\":100},{\"prop\":\"operatorId\",\"originalLabel\":\"制单人\",\"label\":\"制单人\",\"visible\":true,\"width\":100},{\"prop\":\"remark\",\"originalLabel\":\"备注信息\",\"label\":\"备注信息\",\"visible\":true,\"minWidth\":120},{\"prop\":\"status\",\"originalLabel\":\"状态\",\"label\":\"状态\",\"visible\":true,\"width\":100,\"fixed\":\"right\"},{\"prop\":\"actions\",\"originalLabel\":\"相关操作\",\"label\":\"相关操作\",\"visible\":true,\"width\":260,\"fixed\":\"right\"}]',
 '[{\"prop\":\"selection\",\"originalLabel\":\"\",\"label\":\"\",\"visible\":true,\"type\":\"selection\",\"width\":55,\"fixed\":false},{\"prop\":\"storeId\",\"originalLabel\":\"所属门店\",\"label\":\"所属门店\",\"visible\":true,\"width\":100},{\"prop\":\"docDate\",\"originalLabel\":\"单据日期\",\"label\":\"单据日期\",\"visible\":true,\"width\":120},{\"prop\":\"docNo\",\"originalLabel\":\"单据编号\",\"label\":\"单据编号\",\"visible\":true,\"width\":180},{\"prop\":\"customerId\",\"originalLabel\":\"客户\",\"label\":\"客户\",\"visible\":true,\"width\":120},{\"prop\":\"totalAmount\",\"originalLabel\":\"总计金额(元)\",\"label\":\"总计金额(元)\",\"visible\":true,\"width\":120},{\"prop\":\"discountAmount\",\"originalLabel\":\"折扣金额(元)\",\"label\":\"折扣金额(元)\",\"visible\":true,\"width\":120},{\"prop\":\"actualAmount\",\"originalLabel\":\"折后金额(元)\",\"label\":\"折后金额(元)\",\"visible\":true,\"width\":120},{\"prop\":\"paidAmount\",\"originalLabel\":\"已收金额(元)\",\"label\":\"已收金额(元)\",\"visible\":true,\"width\":100},{\"prop\":\"operatorId\",\"originalLabel\":\"制单人\",\"label\":\"制单人\",\"visible\":true,\"width\":100},{\"prop\":\"remark\",\"originalLabel\":\"备注信息\",\"label\":\"备注信息\",\"visible\":true,\"minWidth\":120},{\"prop\":\"status\",\"originalLabel\":\"状态\",\"label\":\"状态\",\"visible\":true,\"width\":100,\"fixed\":\"right\"},{\"prop\":\"actions\",\"originalLabel\":\"相关操作\",\"label\":\"相关操作\",\"visible\":true,\"width\":260,\"fixed\":\"right\"}]',
 1, '销售单列表(按单据)默认列配置'),
('sales_order_list_by_detail', '销售单-按明细', 'list',
 '[{\"prop\":\"selection\",\"originalLabel\":\"\",\"label\":\"\",\"visible\":true,\"type\":\"selection\",\"width\":55},{\"prop\":\"docNo\",\"originalLabel\":\"单据编号\",\"label\":\"单据编号\",\"visible\":true,\"width\":180},{\"prop\":\"qty\",\"originalLabel\":\"数量\",\"label\":\"数量\",\"visible\":true,\"width\":80}]',
 '[{\"prop\":\"selection\",\"originalLabel\":\"\",\"label\":\"\",\"visible\":true,\"type\":\"selection\",\"width\":55},{\"prop\":\"docNo\",\"originalLabel\":\"单据编号\",\"label\":\"单据编号\",\"visible\":true,\"width\":180},{\"prop\":\"qty\",\"originalLabel\":\"数量\",\"label\":\"数量\",\"visible\":true,\"width\":80}]',
 1, '销售单列表(按明细)默认列配置'),
('sales_order_add_select_product', '销售单-选择商品', 'list',
 '[{\"prop\":\"selection\",\"originalLabel\":\"\",\"label\":\"\",\"visible\":true,\"type\":\"selection\",\"width\":55},{\"prop\":\"selectedQty\",\"originalLabel\":\"数量\",\"label\":\"数量\",\"visible\":true,\"width\":80},{\"prop\":\"image\",\"originalLabel\":\"商品图像\",\"label\":\"图像\",\"visible\":true,\"width\":70},{\"prop\":\"name\",\"originalLabel\":\"商品名称\",\"label\":\"商品名称\",\"visible\":true,\"minWidth\":120},{\"prop\":\"attributes\",\"originalLabel\":\"辅助属性\",\"label\":\"辅助属性\",\"visible\":true,\"width\":120},{\"prop\":\"productCode\",\"originalLabel\":\"商品编号\",\"label\":\"商品编号\",\"visible\":false,\"width\":110},{\"prop\":\"spec\",\"originalLabel\":\"规格型号\",\"label\":\"规格型号\",\"visible\":true,\"width\":120},{\"prop\":\"brand\",\"originalLabel\":\"商品品牌\",\"label\":\"品牌\",\"visible\":true,\"width\":90},{\"prop\":\"unit\",\"originalLabel\":\"单位\",\"label\":\"单位\",\"visible\":true,\"width\":70},{\"prop\":\"wholesalePrice\",\"originalLabel\":\"销售价格\",\"label\":\"销售单价\",\"visible\":true,\"width\":110},{\"prop\":\"purchasePrice\",\"originalLabel\":\"进货价格\",\"label\":\"进货价格\",\"visible\":true,\"width\":110},{\"prop\":\"retailPrice\",\"originalLabel\":\"零售价\",\"label\":\"零售价格\",\"visible\":false,\"width\":110},{\"prop\":\"stock\",\"originalLabel\":\"可用库存\",\"label\":\"库存\",\"visible\":true,\"width\":80},{\"prop\":\"category\",\"originalLabel\":\"商品分类\",\"label\":\"分类\",\"visible\":true,\"width\":110},{\"prop\":\"barcode\",\"originalLabel\":\"条形码\",\"label\":\"条形码\",\"visible\":true,\"width\":120},{\"prop\":\"defaultWarehouse\",\"originalLabel\":\"默认仓库\",\"label\":\"默认仓库\",\"visible\":false,\"width\":110},{\"prop\":\"location\",\"originalLabel\":\"商品货位\",\"label\":\"商品货位\",\"visible\":false,\"width\":100},{\"prop\":\"exchangePoints\",\"originalLabel\":\"兑换积分\",\"label\":\"兑换积分\",\"visible\":false,\"width\":100},{\"prop\":\"stockUnit\",\"originalLabel\":\"库存单位\",\"label\":\"库存单位\",\"visible\":false,\"width\":100},{\"prop\":\"stockValue\",\"originalLabel\":\"库存价值\",\"label\":\"库存价值\",\"visible\":false,\"width\":100},{\"prop\":\"remark\",\"originalLabel\":\"备注信息\",\"label\":\"备注信息\",\"visible\":true,\"width\":120}]',
 '[{\"prop\":\"selection\",\"originalLabel\":\"\",\"label\":\"\",\"visible\":true,\"type\":\"selection\",\"width\":55},{\"prop\":\"selectedQty\",\"originalLabel\":\"数量\",\"label\":\"数量\",\"visible\":true,\"width\":80},{\"prop\":\"image\",\"originalLabel\":\"商品图像\",\"label\":\"图像\",\"visible\":true,\"width\":70},{\"prop\":\"name\",\"originalLabel\":\"商品名称\",\"label\":\"商品名称\",\"visible\":true,\"minWidth\":120},{\"prop\":\"attributes\",\"originalLabel\":\"辅助属性\",\"label\":\"辅助属性\",\"visible\":true,\"width\":120},{\"prop\":\"productCode\",\"originalLabel\":\"商品编号\",\"label\":\"商品编号\",\"visible\":false,\"width\":110},{\"prop\":\"spec\",\"originalLabel\":\"规格型号\",\"label\":\"规格型号\",\"visible\":true,\"width\":120},{\"prop\":\"brand\",\"originalLabel\":\"商品品牌\",\"label\":\"品牌\",\"visible\":true,\"width\":90},{\"prop\":\"unit\",\"originalLabel\":\"单位\",\"label\":\"单位\",\"visible\":true,\"width\":70},{\"prop\":\"wholesalePrice\",\"originalLabel\":\"销售价格\",\"label\":\"销售单价\",\"visible\":true,\"width\":110},{\"prop\":\"purchasePrice\",\"originalLabel\":\"进货价格\",\"label\":\"进货价格\",\"visible\":true,\"width\":110},{\"prop\":\"retailPrice\",\"originalLabel\":\"零售价\",\"label\":\"零售价格\",\"visible\":false,\"width\":110},{\"prop\":\"stock\",\"originalLabel\":\"可用库存\",\"label\":\"库存\",\"visible\":true,\"width\":80},{\"prop\":\"category\",\"originalLabel\":\"商品分类\",\"label\":\"分类\",\"visible\":true,\"width\":110},{\"prop\":\"barcode\",\"originalLabel\":\"条形码\",\"label\":\"条形码\",\"visible\":true,\"width\":120},{\"prop\":\"defaultWarehouse\",\"originalLabel\":\"默认仓库\",\"label\":\"默认仓库\",\"visible\":false,\"width\":110},{\"prop\":\"location\",\"originalLabel\":\"商品货位\",\"label\":\"商品货位\",\"visible\":false,\"width\":100},{\"prop\":\"exchangePoints\",\"originalLabel\":\"兑换积分\",\"label\":\"兑换积分\",\"visible\":false,\"width\":100},{\"prop\":\"stockUnit\",\"originalLabel\":\"库存单位\",\"label\":\"库存单位\",\"visible\":false,\"width\":100},{\"prop\":\"stockValue\",\"originalLabel\":\"库存价值\",\"label\":\"库存价值\",\"visible\":false,\"width\":100},{\"prop\":\"remark\",\"originalLabel\":\"备注信息\",\"label\":\"备注信息\",\"visible\":true,\"width\":120}]',
 1, '销售单选择商品默认列配置');

-- ----------------------------
-- Table structure for jxc_customer
-- ----------------------------
DROP TABLE IF EXISTS `jxc_customer`;
CREATE TABLE `jxc_customer` (
  `customer_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) NOT NULL COMMENT '客户名称',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `initial_arrears` decimal(10,2) DEFAULT '0.00' COMMENT '期初欠款',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ----------------------------
-- Table structure for jxc_supplier
-- ----------------------------
DROP TABLE IF EXISTS `jxc_supplier`;
CREATE TABLE `jxc_supplier` (
  `supplier_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(100) NOT NULL COMMENT '供应商名称',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `initial_arrears` decimal(10,2) DEFAULT '0.00' COMMENT '期初欠款(应付)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- ----------------------------
-- Table structure for jxc_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `jxc_warehouse`;
CREATE TABLE `jxc_warehouse` (
  `warehouse_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `warehouse_name` varchar(100) NOT NULL COMMENT '仓库名称',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- ----------------------------
-- Table structure for jxc_inventory
-- ----------------------------
DROP TABLE IF EXISTS `jxc_inventory`;
CREATE TABLE `jxc_inventory` (
  `inventory_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `qty` int(11) NOT NULL DEFAULT '0' COMMENT '当前库存数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `idx_warehouse_sku` (`warehouse_id`, `sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- ----------------------------
-- Table structure for jxc_purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `jxc_purchase_order`;
CREATE TABLE `jxc_purchase_order` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `doc_no` varchar(50) NOT NULL COMMENT '单据编号',
  `order_type` tinyint(2) NOT NULL COMMENT '类型 (1:进货单, 2:进货预订单, 3:进货退货单)',
  `doc_date` date NOT NULL COMMENT '业务日期',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `status` tinyint(2) DEFAULT '10' COMMENT '状态 (10:草稿, 20:已审核/执行中, 40:已完成)',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '总金额',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '已付金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `idx_doc_no` (`doc_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进货单表';

-- ----------------------------
-- Table structure for jxc_purchase_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `jxc_purchase_order_detail`;
CREATE TABLE `jxc_purchase_order_detail` (
  `detail_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL,
  `sku_id` bigint(20) NOT NULL,
  `spu_id` bigint(20) NOT NULL,
  `qty` int(11) NOT NULL DEFAULT '0',
  `unit_price` decimal(10,2) DEFAULT '0.00',
  `line_amount` decimal(10,2) DEFAULT '0.00',
  `warehouse_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`detail_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进货单明细表';

-- ----------------------------
-- Table structure for jxc_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `jxc_sys_user`;
CREATE TABLE `jxc_sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '职员账号',
  `real_name` varchar(50) NOT NULL COMMENT '职员名称',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `store_id` bigint(20) DEFAULT NULL COMMENT '所属门店ID',
  `store_name` varchar(100) DEFAULT NULL COMMENT '所属门店名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  `permissions` text DEFAULT NULL COMMENT '权限配置JSON',
  `data_auth` text DEFAULT NULL COMMENT '数据授权JSON',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户/职员表';

-- ----------------------------
-- Table structure for jxc_print_template
-- ----------------------------
DROP TABLE IF EXISTS `jxc_print_template`;
CREATE TABLE `jxc_print_template` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `biz_type` varchar(50) NOT NULL COMMENT '业务类型',
  `paper_type` varchar(50) DEFAULT NULL COMMENT '纸张类型',
  `paper_width_mm` int(11) DEFAULT NULL COMMENT '纸张宽(mm)',
  `paper_height_mm` int(11) DEFAULT NULL COMMENT '纸张高(mm)',
  `design_json` mediumtext DEFAULT NULL COMMENT '设计JSON',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认(0否1是)',
  `enabled` tinyint(1) DEFAULT '1' COMMENT '启用(0否1是)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`template_id`),
  KEY `idx_biz_type` (`biz_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印模板表';

-- ----------------------------
-- Table structure for jxc_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `jxc_oper_log`;
CREATE TABLE `jxc_oper_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_id` bigint(20) DEFAULT NULL COMMENT '所属门店ID',
  `store_name` varchar(100) DEFAULT NULL COMMENT '所属门店',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人',
  `title` varchar(100) DEFAULT NULL COMMENT '模块',
  `action` varchar(200) DEFAULT NULL COMMENT '操作',
  `content` varchar(500) DEFAULT NULL COMMENT '操作内容',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `request_ip` varchar(50) DEFAULT NULL COMMENT '请求IP',
  `request_params` mediumtext DEFAULT NULL COMMENT '请求参数JSON',
  `response_data` mediumtext DEFAULT NULL COMMENT '响应JSON',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0失败1成功)',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误消息',
  `cost_time` bigint(20) DEFAULT NULL COMMENT '耗时(ms)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`log_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- Table structure for jxc_finance_account
-- ----------------------------
DROP TABLE IF EXISTS `jxc_finance_account`;
CREATE TABLE `jxc_finance_account` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(50) NOT NULL COMMENT '账户名称(如微信、支付宝、现金)',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金账户表';

-- ----------------------------
-- Table structure for jxc_product_attribute
-- ----------------------------
DROP TABLE IF EXISTS `jxc_product_attribute`;
CREATE TABLE `jxc_product_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '属性名称',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0停用1启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

-- ----------------------------
-- Table structure for jxc_product_attribute_value
-- ----------------------------
DROP TABLE IF EXISTS `jxc_product_attribute_value`;
CREATE TABLE `jxc_product_attribute_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` bigint(20) NOT NULL COMMENT '属性ID',
  `value` varchar(100) NOT NULL COMMENT '属性值',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_by` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_attribute_id` (`attribute_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性值表';
