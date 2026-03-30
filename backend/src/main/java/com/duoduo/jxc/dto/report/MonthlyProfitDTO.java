package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 月度利润DTO
 */
@Data
public class MonthlyProfitDTO {

    private String month;
    private Integer salesQuantity;
    private BigDecimal salesAmount;
    private BigDecimal costAmount;
    private BigDecimal grossProfit;
    private BigDecimal grossProfitRate;
}
