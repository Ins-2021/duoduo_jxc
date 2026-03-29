package com.duoduo.jxc.dto.cost;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CostPeriodQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String periodNo;
    private String yearMonth;
    /** 状态: 0=open, 1=calculated, 2=closed */
    private Integer status;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
}
