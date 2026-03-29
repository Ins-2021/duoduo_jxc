package com.duoduo.jxc.dto.cost;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCostDTO {
    private Long productCostId;
    private String costNo;
    private String accountPeriod;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private Long productionId;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal overheadCost;
    private BigDecimal totalCost;
    private BigDecimal outputQuantity;
    private BigDecimal unitCost;
    private Integer status;
    private String remark;
}
