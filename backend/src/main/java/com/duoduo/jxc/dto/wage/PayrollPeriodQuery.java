package com.duoduo.jxc.dto.wage;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PayrollPeriodQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String sheetNo;
    private String yearMonth;
    /** 状态: 0=draft, 1=pending, 2=approved, 3=paid */
    private Integer status;
    private LocalDate startDateFrom;
    private LocalDate startDateTo;
    private LocalDate endDateFrom;
    private LocalDate endDateTo;
}
