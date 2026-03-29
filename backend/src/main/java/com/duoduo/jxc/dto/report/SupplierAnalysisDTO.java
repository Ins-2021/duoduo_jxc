package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SupplierAnalysisDTO {
    private Long supplierId;
    private String supplierName;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal unpaidAmount;
    private BigDecimal onTimeRate;
    private BigDecimal qualityRate;
}
