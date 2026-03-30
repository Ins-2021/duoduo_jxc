package com.duoduo.jxc.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 客户利润DTO
 */
@Data
public class CustomerProfitDTO {

    private Long customerId;
    private String customerCode;
    private String customerName;
    private Integer salesQuantity;
    private BigDecimal salesAmount;
    private BigDecimal costAmount;
    private BigDecimal grossProfit;
    private BigDecimal grossProfitRate;
    private Integer orderCount;
}
