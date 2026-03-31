DROP TABLE IF EXISTS jxc_capacity_alert;
CREATE TABLE jxc_capacity_alert (
  alert_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  factory_id BIGINT,
  process_id BIGINT,
  alert_type VARCHAR(50),
  alert_level VARCHAR(20),
  alert_content TEXT,
  alert_time DATETIME,
  status VARCHAR(20),
  handle_time DATETIME,
  handler_id BIGINT,
  handle_remark TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_by BIGINT,
  update_by BIGINT,
  deleted INT DEFAULT 0
);
