package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductionScheduleQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String scheduleNo;
    private Long orderId;
    private Long planId;
    /** 状态: draft/confirmed/in_progress/completed/cancelled */
    private String status;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
}
