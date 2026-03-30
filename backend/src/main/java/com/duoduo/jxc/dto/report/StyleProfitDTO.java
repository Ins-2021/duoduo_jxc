package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 款式利润DTO
 */
@Data
public class StyleProfitDTO {

    private Long styleId;
    private String styleNo;
    private String styleName;
    private Integer salesQuantity;      // 销售数量
    private BigDecimal salesAmount;     // 销售金额
    private BigDecimal costAmount;      // 成本金额
    private BigDecimal grossProfit;     // 毛利
    private BigDecimal grossProfitRate; // 毛利率
    private Integer orderCount;         // 订单数
}
