package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseDailyReportDTO {
    private LocalDate reportDate;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private BigDecimal paidAmount;
    private Integer supplierCount;
    private BigDecimal avgOrderAmount;
}
