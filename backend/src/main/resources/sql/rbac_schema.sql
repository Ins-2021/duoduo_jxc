CREATE TABLE IF NOT EXISTS jxc_sys_user (
  user_id BIGINT NOT NULL AUTO_INCREMENT,
  dept_id BIGINT NULL COMMENT '部门ID',
  username VARCHAR(64) NOT NULL COMMENT '账号',
  real_name VARCHAR(64) NULL COMMENT '姓名',
  password VARCHAR(255) NULL COMMENT '密码(加密)',
  store_id BIGINT NULL COMMENT '门店ID',
  store_name VARCHAR(128) NULL COMMENT '门店名称',
  remark VARCHAR(255) NULL COMMENT '备注',
  permissions TEXT NULL COMMENT '兼容字段(逗号分隔权限)',
  data_auth TEXT NULL COMMENT '数据权限JSON/预留',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_username (username),
  KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS jxc_sys_role (
  role_id BIGINT NOT NULL AUTO_INCREMENT,
  role_key VARCHAR(64) NOT NULL COMMENT '角色标识',
  role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
  data_scope CHAR(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  remark VARCHAR(255) NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (role_id),
  UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS jxc_sys_dept (
  dept_id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
  ancestors VARCHAR(255) NULL COMMENT '祖级列表',
  dept_name VARCHAR(64) NOT NULL COMMENT '部门名称',
  order_num INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  leader VARCHAR(64) NULL COMMENT '负责人',
  phone VARCHAR(20) NULL COMMENT '联系电话',
  email VARCHAR(64) NULL COMMENT '邮箱',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (dept_id),
  KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS jxc_sys_post (
  post_id BIGINT NOT NULL AUTO_INCREMENT,
  post_code VARCHAR(64) NOT NULL COMMENT '岗位编码',
  post_name VARCHAR(64) NOT NULL COMMENT '岗位名称',
  post_sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  remark VARCHAR(255) NULL COMMENT '备注',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (post_id),
  UNIQUE KEY uk_post_code (post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

CREATE TABLE IF NOT EXISTS jxc_sys_user_post (
  user_id BIGINT NOT NULL,
  post_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

CREATE TABLE IF NOT EXISTS jxc_sys_role_dept (
  role_id BIGINT NOT NULL,
  dept_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色部门关联表';

CREATE TABLE IF NOT EXISTS jxc_sys_menu (
  menu_id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
  order_num INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  path VARCHAR(255) NULL COMMENT '路由地址',
  component VARCHAR(255) NULL COMMENT '组件路径',
  route_name VARCHAR(128) NULL COMMENT '路由名称',
  icon VARCHAR(64) NULL COMMENT '菜单图标',
  menu_type CHAR(1) NOT NULL DEFAULT 'M' COMMENT '类型(M目录 C菜单 F按钮)',
  visible TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见(0否1是)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0停用1启用)',
  perms VARCHAR(128) NULL COMMENT '权限标识',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (menu_id),
  KEY idx_menu_parent (parent_id),
  KEY idx_menu_type (menu_type),
  KEY idx_menu_perms (perms)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE IF NOT EXISTS jxc_sys_user_role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  create_time DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS jxc_sys_role_menu (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  create_time DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_menu (role_id, menu_id),
  KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
