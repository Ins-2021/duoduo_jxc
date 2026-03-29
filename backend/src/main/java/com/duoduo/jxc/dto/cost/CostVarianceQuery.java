package com.duoduo.jxc.dto.cost;

import lombok.Data;

@Data
public class CostVarianceQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String varianceNo;
    private String accountPeriod;
    private Long productionId;
    private Long styleId;
    private String styleCode;
    /** 状态: 0=pending, 1=analyzed */
    private Integer status;
}
