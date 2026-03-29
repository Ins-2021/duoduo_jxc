package com.duoduo.jxc.dto.cost;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductCostQuery extends PageQuery {
    private String costNo;
    private String accountPeriod;
    private Long styleId;
    private String styleCode;
    private Long productionId;
    private Integer status;
}
