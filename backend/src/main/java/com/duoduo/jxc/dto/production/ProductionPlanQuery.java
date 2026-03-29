package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductionPlanQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String planNo;
    private Long orderId;
    /** 状态: draft/confirmed/in_progress/completed/cancelled */
    private String status;
    private LocalDate plannedDateFrom;
    private LocalDate plannedDateTo;
}
