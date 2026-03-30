-- 产能预警规则表
CREATE TABLE IF NOT EXISTS capacity_alert_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    metric_type VARCHAR(20) NOT NULL COMMENT '指标类型(utilization-利用率,backlog-积压,delay-延期)',
    factory_id BIGINT COMMENT '工厂ID',
    work_group_id BIGINT COMMENT '工作组ID',
    process_id BIGINT COMMENT '工序ID',
    warning_threshold DECIMAL(10,2) COMMENT '警告阈值',
    critical_threshold DECIMAL(10,2) COMMENT '严重阈值',
    notify_type VARCHAR(50) COMMENT '通知类型',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态(active-启用,inactive-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)'
) COMMENT '产能预警规则表';

-- 产能预警记录表
CREATE TABLE IF NOT EXISTS capacity_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    alert_type VARCHAR(20) NOT NULL COMMENT '预警类型',
    alert_level VARCHAR(20) NOT NULL COMMENT '预警级别(warning-警告,critical-严重)',
    message VARCHAR(500) COMMENT '预警消息',
    factory_id BIGINT COMMENT '工厂ID',
    factory_name VARCHAR(100) COMMENT '工厂名称',
    work_group_id BIGINT COMMENT '工作组ID',
    work_group_name VARCHAR(100) COMMENT '工作组名称',
    process_id BIGINT COMMENT '工序ID',
    process_name VARCHAR(100) COMMENT '工序名称',
    order_id BIGINT COMMENT '订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    metric_value DECIMAL(10,2) COMMENT '当前指标值',
    threshold DECIMAL(10,2) COMMENT '阈值',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态(active-活跃,acknowledged-已确认,resolved-已解决)',
    acknowledged_by BIGINT COMMENT '确认人ID',
    acknowledged_at DATETIME COMMENT '确认时间',
    resolution VARCHAR(500) COMMENT '解决方案',
    resolved_by BIGINT COMMENT '解决人ID',
    resolved_at DATETIME COMMENT '解决时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '删除标记(0-未删除,1-已删除)'
) COMMENT '产能预警记录表';

-- 创建索引
CREATE INDEX idx_alert_status ON capacity_alert(status);
CREATE INDEX idx_alert_factory ON capacity_alert(factory_id);
CREATE INDEX idx_alert_create_time ON capacity_alert(create_time);
CREATE INDEX idx_alert_rule_status ON capacity_alert_rule(status);

-- 插入默认预警规则
INSERT INTO capacity_alert_rule (rule_name, metric_type, factory_id, warning_threshold, critical_threshold, notify_type, status) VALUES
('工厂A产能利用率预警', 'utilization', 1, 80.00, 90.00, 'system', 'active'),
('工厂A积压预警', 'backlog', 1, 3.00, 5.00, 'system', 'active');

CREATE TABLE jxc_bom_color_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bom_id BIGINT NOT NULL,
    bom_item_id BIGINT NOT NULL,
    product_color VARCHAR(50) NOT NULL,
    actual_material_id BIGINT NOT NULL,
    actual_material_color VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0,
    INDEX idx_bom_item_id (bom_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM颜色映射规则';

CREATE TABLE jxc_bom_size_mapping (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bom_id BIGINT NOT NULL,
    bom_item_id BIGINT NOT NULL,
    product_size VARCHAR(50) NOT NULL,
    actual_material_id BIGINT NOT NULL,
    actual_usage DECIMAL(10,4),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT,
    update_by BIGINT,
    deleted TINYINT DEFAULT 0,
    INDEX idx_bom_item_id (bom_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM尺码映射规则';
