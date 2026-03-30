package com.duoduo.jxc.dto.report;

import lombok.Data;

/**
 * 工序瓶颈DTO
 */
@Data
public class ProcessBottleneckDTO {

    private Long processId;
    private String processName;
    private Integer pendingQuantity;
    private Integer dailyCapacity;
    private Integer estimatedDays;
}
