package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 成本核算期间
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_cost_period")
public class CostPeriod {

    @TableId(type = IdType.AUTO)
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
