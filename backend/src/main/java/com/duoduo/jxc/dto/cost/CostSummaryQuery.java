package com.duoduo.jxc.dto.cost;

import lombok.Data;

@Data
public class CostSummaryQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String summaryNo;
    private String accountPeriod;
    private Long productionId;
    private Long styleId;
    private String styleCode;
    /** 状态: 0=pending, 1=completed */
    private Integer status;
}
