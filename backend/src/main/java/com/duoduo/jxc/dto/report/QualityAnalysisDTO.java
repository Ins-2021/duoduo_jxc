package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QualityAnalysisDTO {
    private Long productId;
    private String productName;
    private Integer totalQuantity;
    private Integer qualifiedQuantity;
    private Integer defectQuantity;
    private BigDecimal qualityRate;
    private BigDecimal defectRate;
    private String mainDefect;
}
