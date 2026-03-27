USE duoduo_jxc;
DROP TABLE IF EXISTS `jxc_sys_user`;
CREATE TABLE `jxc_sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '职员账号',
  `real_name` varchar(50) NOT NULL COMMENT '职员名称',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
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

INSERT INTO `jxc_sys_user` (`username`, `real_name`, `store_id`, `store_name`, `remark`) VALUES 
('制单员', '景海静', 1, '衣多多', ''),
('15210629985', '管理员', 1, '衣多多', '');
