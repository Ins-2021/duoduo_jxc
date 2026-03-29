package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionScheduleDTO {
    private Long scheduleId;
    private String scheduleNo;
    private Long orderId;
    private Long planId;
    private LocalDate startDate;
    private LocalDate endDate;
    /** JSON格式的排程明细 */
    private String scheduleItems;
    /** 状态: draft/confirmed/in_progress/completed/cancelled */
    private String status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
}
