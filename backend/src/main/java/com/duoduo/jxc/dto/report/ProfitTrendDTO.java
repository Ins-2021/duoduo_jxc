package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 利润趋势DTO
 */
@Data
public class ProfitTrendDTO {

    private List<String> dates;
    private List<BigDecimal> salesAmount;
    private List<BigDecimal> costAmount;
    private List<BigDecimal> grossProfit;
    private BigDecimal totalSalesAmount;
    private BigDecimal totalCostAmount;
    private BigDecimal totalGrossProfit;
    private BigDecimal avgGrossProfitRate;
}
