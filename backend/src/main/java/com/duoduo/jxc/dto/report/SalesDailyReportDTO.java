package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesDailyReportDTO {
    private LocalDate reportDate;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private BigDecimal refundAmount;
    private BigDecimal netAmount; // 净销售额 = actualAmount - refundAmount
}
