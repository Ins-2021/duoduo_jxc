package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceivableReportDTO {
    private Long customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal unpaidAmount;
    private BigDecimal overdueAmount;
    private Integer overdueDays;
}
