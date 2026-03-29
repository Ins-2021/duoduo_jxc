package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CutOrderQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String planNo;
    private Long orderId;
    private Long fabricId;
    /** 状态: pending/in_progress/completed/cancelled */
    private String status;
    private LocalDate cuttingDateFrom;
    private LocalDate cuttingDateTo;
}
