package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class SalesMonthlyReportDTO {
    private YearMonth reportMonth;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private BigDecimal paidAmount;
    private Integer customerCount;
    private BigDecimal avgOrderAmount;
    private BigDecimal monthOnMonthRate;
    private BigDecimal yearOnYearRate;
}
