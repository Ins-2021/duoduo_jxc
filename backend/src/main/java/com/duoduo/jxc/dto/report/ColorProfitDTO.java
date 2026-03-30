package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 颜色利润DTO
 */
@Data
public class ColorProfitDTO {

    private String colorCode;
    private String colorName;
    private Integer salesQuantity;
    private BigDecimal salesAmount;
    private BigDecimal costAmount;
    private BigDecimal grossProfit;
    private BigDecimal grossProfitRate;
}
