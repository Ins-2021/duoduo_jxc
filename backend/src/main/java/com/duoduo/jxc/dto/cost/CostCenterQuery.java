package com.duoduo.jxc.dto.cost;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CostCenterQuery extends PageQuery {
    private String centerCode;
    private String centerName;
    private Integer centerType;
    private Integer status;
}
