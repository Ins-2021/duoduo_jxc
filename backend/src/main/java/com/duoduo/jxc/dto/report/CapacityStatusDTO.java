package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 产能状态DTO
 */
@Data
public class CapacityStatusDTO {

    private Long factoryId;
    private String factoryName;
    private Long processId;
    private String processName;
    private Integer dailyCapacity;
    private Integer currentLoad;
    private BigDecimal utilizationRate;
    private Integer backlogQuantity;
    private Integer backlogDays;
    private String status;
}
