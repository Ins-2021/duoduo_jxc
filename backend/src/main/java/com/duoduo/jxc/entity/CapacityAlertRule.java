package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产能预警规则实体
 */
@Data
@TableName("jxc_capacity_alert_rule")
public class CapacityAlertRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleName;
    private String metricType;
    private Long factoryId;
    private Long workGroupId;
    private Long processId;
    private BigDecimal warningThreshold;
    private BigDecimal criticalThreshold;
    private String notifyType;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
