package com.duoduo.jxc.dto.cost;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CostPoolQuery extends PageQuery {
    private String poolNo;
    private String accountPeriod;
    private Integer costType;
    private Long costCenterId;
    private Long styleId;
    private Integer status;
}
