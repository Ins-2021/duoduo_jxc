CREATE TABLE IF NOT EXISTS jxc_wf_model (
  id BIGINT NOT NULL AUTO_INCREMENT,
  model_key VARCHAR(128) NOT NULL,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(128) NULL,
  bpmn_xml LONGTEXT NOT NULL,
  version INT NOT NULL DEFAULT 1,
  status VARCHAR(32) NOT NULL DEFAULT 'draft',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_model_key (model_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_model_publish (
  id BIGINT NOT NULL AUTO_INCREMENT,
  model_id BIGINT NOT NULL,
  deployment_id VARCHAR(64) NOT NULL,
  process_def_id VARCHAR(64) NOT NULL,
  process_def_key VARCHAR(128) NOT NULL,
  process_def_version INT NOT NULL,
  published_by BIGINT NULL,
  published_at DATETIME NULL,
  remark VARCHAR(255) NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_wf_publish_model_id (model_id),
  KEY idx_wf_publish_def_key_version (process_def_key, process_def_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_biz_process_binding (
  id BIGINT NOT NULL AUTO_INCREMENT,
  biz_type VARCHAR(64) NOT NULL,
  process_def_key VARCHAR(128) NOT NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  start_condition TEXT NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_binding_biz_type (biz_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_instance (
  id BIGINT NOT NULL AUTO_INCREMENT,
  proc_inst_id VARCHAR(64) NOT NULL,
  business_key VARCHAR(128) NOT NULL,
  biz_type VARCHAR(64) NOT NULL,
  biz_id VARCHAR(64) NOT NULL,
  title VARCHAR(255) NULL,
  initiator_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'running',
  start_time DATETIME NULL,
  end_time DATETIME NULL,
  current_task_names VARCHAR(512) NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_instance_proc_inst_id (proc_inst_id),
  KEY idx_wf_instance_biz (biz_type, biz_id),
  KEY idx_wf_instance_initiator (initiator_id),
  KEY idx_wf_instance_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_task (
  id BIGINT NOT NULL AUTO_INCREMENT,
  task_id VARCHAR(64) NOT NULL,
  proc_inst_id VARCHAR(64) NOT NULL,
  biz_type VARCHAR(64) NOT NULL,
  biz_id VARCHAR(64) NOT NULL,
  node_key VARCHAR(128) NOT NULL,
  node_name VARCHAR(255) NOT NULL,
  assignee_id BIGINT NULL,
  candidate_summary VARCHAR(512) NULL,
  due_time DATETIME NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'todo',
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_wf_task_task_id (task_id),
  KEY idx_wf_task_proc_inst (proc_inst_id),
  KEY idx_wf_task_biz (biz_type, biz_id),
  KEY idx_wf_task_assignee_status (assignee_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS jxc_wf_task_action_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  proc_inst_id VARCHAR(64) NOT NULL,
  task_id VARCHAR(64) NOT NULL,
  action VARCHAR(32) NOT NULL,
  operator_id BIGINT NOT NULL,
  comment TEXT NULL,
  attachments_json TEXT NULL,
  created_at DATETIME NULL,
  create_time DATETIME NULL,
  update_time DATETIME NULL,
  create_by BIGINT NULL,
  update_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_wf_action_proc_inst (proc_inst_id),
  KEY idx_wf_action_task_id (task_id),
  KEY idx_wf_action_operator (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

