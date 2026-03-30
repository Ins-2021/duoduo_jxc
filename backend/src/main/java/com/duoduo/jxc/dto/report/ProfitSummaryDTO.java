package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 利润汇总DTO
 */
@Data
public class ProfitSummaryDTO {

    private BigDecimal totalSalesAmount;
    private BigDecimal totalCostAmount;
    private BigDecimal totalGrossProfit;
    private BigDecimal avgGrossProfitRate;
    private Integer totalOrderCount;
    private Integer totalSalesQuantity;
}
