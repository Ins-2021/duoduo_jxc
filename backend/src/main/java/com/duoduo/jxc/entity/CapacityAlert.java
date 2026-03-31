package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产能预警记录实体
 */
@Data
@TableName("jxc_capacity_alert")
public class CapacityAlert {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String alertType;
    private String alertLevel;
    private String message;
    private Long factoryId;
    private String factoryName;
    private Long workGroupId;
    private String workGroupName;
    private Long processId;
    private String processName;
    private Long orderId;
    private String orderNo;
    private BigDecimal metricValue;
    private BigDecimal threshold;
    private String status;
    private Long acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    private String resolution;
    private Long resolvedBy;
    private LocalDateTime resolvedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
