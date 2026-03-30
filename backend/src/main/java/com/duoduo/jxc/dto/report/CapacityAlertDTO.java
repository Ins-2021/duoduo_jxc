package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产能预警DTO
 */
@Data
public class CapacityAlertDTO {

    private Long alertId;
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
    private LocalDateTime createdAt;
}
