package com.duoduo.jxc.dto.cost;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CostPoolDTO {
    private Long poolId;
    private String poolNo;
    private String accountPeriod;
    private Integer costType;
    private Long costCenterId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private Long productionId;
    private BigDecimal totalAmount;
    private BigDecimal allocatedAmount;
    private BigDecimal remainingAmount;
    private Integer status;
    private String remark;
}
