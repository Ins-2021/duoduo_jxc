package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionPlanDTO {
    private Long planId;
    private String planNo;
    private Long orderId;
    private LocalDate plannedDate;
    private Integer plannedQuantity;
    private Integer actualQuantity;
    private Long cuttingGroupId;
    private Long sewingGroupId;
    /** 状态: draft/confirmed/in_progress/completed/cancelled */
    private String status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
}
