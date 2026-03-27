-- RBAC schema migration (idempotent via continue-on-error)
-- 1) sys_user add dept_id
ALTER TABLE jxc_sys_user
  ADD COLUMN dept_id BIGINT NULL COMMENT '部门ID' AFTER user_id;

ALTER TABLE jxc_sys_user
  ADD INDEX idx_dept_id (dept_id);

-- 2) sys_role add data_scope
ALTER TABLE jxc_sys_role
  ADD COLUMN data_scope CHAR(1) DEFAULT '1' COMMENT '数据范围（1：全部 2：自定义 3：本部门 4：本部门及以下 5：仅本人）' AFTER role_name;

-- 3) dept/post tables
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

