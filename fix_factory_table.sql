CREATE TABLE IF NOT EXISTS jxc_factory (
  factory_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  factory_name VARCHAR(100) NOT NULL,
  factory_code VARCHAR(50),
  contact_name VARCHAR(50),
  contact_phone VARCHAR(50),
  address VARCHAR(200),
  remark VARCHAR(500),
  status INT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_by BIGINT,
  update_by BIGINT,
  deleted INT DEFAULT 0
);
