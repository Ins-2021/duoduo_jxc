package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 尺码利润DTO
 */
@Data
public class SizeProfitDTO {

    private String sizeName;
    private Integer salesQuantity;
    private BigDecimal salesAmount;
    private BigDecimal costAmount;
    private BigDecimal grossProfit;
    private BigDecimal grossProfitRate;
}
