package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryTurnoverDTO {
    private Long spuId;
    private String spuName;
    private String spuCode;
    private BigDecimal avgInventoryQuantity;
    private BigDecimal salesQuantity;
    private BigDecimal turnoverRate;
    private Integer turnoverDays;
    private BigDecimal salesAmount;
    private BigDecimal costAmount;
}
