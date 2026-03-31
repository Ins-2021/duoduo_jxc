-- =====================================================
-- 添加工作流缺失的表
-- =====================================================

-- -----------------------------------------------------
-- 1. 流程模型表
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS jxc_wf_model (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    model_key VARCHAR(100) NOT NULL COMMENT '流程Key',
    name VARCHAR(200) NOT NULL COMMENT '流程名称',
    category VARCHAR(100) COMMENT '分类',
    bpmn_xml LONGTEXT COMMENT 'BPMN XML',
    version INT DEFAULT 1 COMMENT '版本号',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿，published-已发布',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_model_key (model_key),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='流程模型表';

-- -----------------------------------------------------
-- 2. 流程实例表
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS jxc_wf_instance (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    proc_inst_id VARCHAR(64) NOT NULL COMMENT 'Flowable流程实例ID',
    business_key VARCHAR(200) COMMENT '业务Key',
    biz_type VARCHAR(50) COMMENT '业务类型',
    biz_id VARCHAR(64) COMMENT '业务ID',
    title VARCHAR(500) COMMENT '流程标题',
    initiator_id BIGINT COMMENT '发起人ID',
    status VARCHAR(20) DEFAULT 'running' COMMENT '状态：running-进行中，completed-已完成，rejected-已驳回',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    current_task_names VARCHAR(500) COMMENT '当前任务名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_proc_inst_id (proc_inst_id),
    INDEX idx_biz_type_id (biz_type, biz_id),
    INDEX idx_initiator (initiator_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='流程实例表';

-- -----------------------------------------------------
-- 3. 模型发布记录表
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS jxc_wf_model_publish (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    model_id BIGINT NOT NULL COMMENT '模型ID',
    deployment_id VARCHAR(64) COMMENT 'Flowable部署ID',
    process_def_id VARCHAR(64) COMMENT '流程定义ID',
    process_def_key VARCHAR(100) COMMENT '流程定义Key',
    process_def_version INT COMMENT '流程定义版本',
    published_by BIGINT COMMENT '发布人',
    published_at DATETIME COMMENT '发布时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    INDEX idx_model_id (model_id),
    INDEX idx_process_def_id (process_def_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='模型发布记录表';

-- -----------------------------------------------------
-- 4. 任务表
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS jxc_wf_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    task_id VARCHAR(64) NOT NULL COMMENT 'Flowable任务ID',
    proc_inst_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    biz_type VARCHAR(50) COMMENT '业务类型',
    biz_id VARCHAR(64) COMMENT '业务ID',
    node_key VARCHAR(100) COMMENT '节点Key',
    node_name VARCHAR(200) COMMENT '节点名称',
    assignee_id BIGINT COMMENT '办理人ID',
    candidate_summary VARCHAR(500) COMMENT '候选人摘要',
    due_time DATETIME COMMENT '截止时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待处理，processing-处理中，completed-已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_id (task_id),
    INDEX idx_proc_inst_id (proc_inst_id),
    INDEX idx_assignee (assignee_id),
    INDEX idx_biz_type_id (biz_type, biz_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='任务表';

-- -----------------------------------------------------
-- 5. 任务操作日志表
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS jxc_wf_task_action_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    proc_inst_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    task_id VARCHAR(64) COMMENT '任务ID',
    action VARCHAR(50) NOT NULL COMMENT '操作：approve-同意，reject-驳回，transfer-转办',
    operator_id BIGINT COMMENT '操作人ID',
    comment VARCHAR(1000) COMMENT '审批意见',
    attachments_json JSON COMMENT '附件JSON',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_proc_inst_id (proc_inst_id),
    INDEX idx_task_id (task_id),
    INDEX idx_operator (operator_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='任务操作日志表';

-- -----------------------------------------------------
-- 6. 扩展 jxc_wf_biz_process_binding 表（如果不存在某些字段）
-- -----------------------------------------------------
-- 检查并添加缺失的列
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS add_wf_binding_columns()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'jxc_wf_biz_process_binding' 
                   AND COLUMN_NAME = 'create_time') THEN
        ALTER TABLE jxc_wf_biz_process_binding ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'jxc_wf_biz_process_binding' 
                   AND COLUMN_NAME = 'update_time') THEN
        ALTER TABLE jxc_wf_biz_process_binding ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'jxc_wf_biz_process_binding' 
                   AND COLUMN_NAME = 'deleted') THEN
        ALTER TABLE jxc_wf_biz_process_binding ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '删除标记';
    END IF;
END$$

DELIMITER ;

CALL add_wf_binding_columns();
DROP PROCEDURE IF EXISTS add_wf_binding_columns;

SELECT '工作流表创建完成' AS result;
