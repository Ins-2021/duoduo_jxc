package com.duoduo.jxc.dto.cost;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CostAllocationDTO {
    private Long allocationId;
    private String allocationNo;
    private String accountPeriod;
    private Long poolId;
    private Integer costType;
    private Long fromCenterId;
    private Long toCenterId;
    private Long styleId;
    private String styleCode;
    private Long productionId;
    private Integer allocateMethod;
    private BigDecimal allocateBase;
    private BigDecimal totalBase;
    private BigDecimal allocateRate;
    private BigDecimal allocateAmount;
    private Integer status;
    private String remark;
}
