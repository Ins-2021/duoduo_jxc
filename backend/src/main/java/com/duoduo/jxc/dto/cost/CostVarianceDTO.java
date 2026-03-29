package com.duoduo.jxc.dto.cost;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CostVarianceDTO {
    private Long varianceId;
    private String varianceNo;
    private String accountPeriod;
    private Long productionId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private BigDecimal actualMaterialCost;
    private BigDecimal standardMaterialCost;
    private BigDecimal materialPriceVariance;
    private BigDecimal materialUsageVariance;
    private BigDecimal materialTotalVariance;
    private BigDecimal actualLaborCost;
    private BigDecimal standardLaborCost;
    private BigDecimal laborRateVariance;
    private BigDecimal laborEfficiencyVariance;
    private BigDecimal laborTotalVariance;
    private BigDecimal actualOverheadCost;
    private BigDecimal standardOverheadCost;
    private BigDecimal overheadBudgetVariance;
    private BigDecimal overheadEfficiencyVariance;
    private BigDecimal overheadTotalVariance;
    private BigDecimal totalVariance;
    private BigDecimal varianceRate;
    private String analysisRemark;
    private String improvementMeasures;
    private LocalDateTime analyzeTime;
    private Long analyzeBy;
    /** 状态: 0=pending, 1=analyzed */
    private Integer status;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
