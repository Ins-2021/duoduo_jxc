package com.duoduo.jxc.dto.cost;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CostAllocationQuery extends PageQuery {
    private String allocationNo;
    private String accountPeriod;
    private Integer costType;
    private Long poolId;
    private Long fromCenterId;
    private Long toCenterId;
    private Long styleId;
    private Integer status;
}
