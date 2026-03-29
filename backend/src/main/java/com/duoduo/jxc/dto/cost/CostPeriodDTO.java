package com.duoduo.jxc.dto.cost;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CostPeriodDTO {
    private Long periodId;
    private String periodNo;
    private String yearMonth;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer productionCount;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal overheadCost;
    private BigDecimal totalCost;
    /** 状态: 0=open, 1=calculated, 2=closed */
    private Integer status;
    private LocalDateTime calculateTime;
    private LocalDateTime closeTime;
    private Long closeBy;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
