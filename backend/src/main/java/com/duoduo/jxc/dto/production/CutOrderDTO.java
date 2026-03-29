package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CutOrderDTO {
    private Long planId;
    private String planNo;
    private Long orderId;
    private Long fabricId;
    private Long markerId;
    private Integer plannedQuantity;
    private Integer cutQuantity;
    private LocalDate cuttingDate;
    private Long cuttingGroupId;
    private String cuttingMachine;
    /** 铺布方式: one_way/round_trip/folded */
    private String spreadMethod;
    private BigDecimal fabricConsumption;
    private BigDecimal fabricDefectRate;
    private BigDecimal utilizationRate;
    private BigDecimal actualEfficiency;
    private Integer cutPieces;
    /** 状态: pending/in_progress/completed/cancelled */
    private String status;
    private String remark;
    private Long createBy;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
}
