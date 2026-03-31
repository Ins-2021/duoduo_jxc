package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成本汇总
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_cost_summary")
public class CostSummary {

    @TableId(value = "id", type = IdType.AUTO)
    private Long summaryId;
    private String summaryNo;
    private String accountPeriod;
    private Long productionId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private BigDecimal outputQuantity;
    private BigDecimal wipQuantity;
    private BigDecimal materialProgress;
    private BigDecimal laborProgress;
    private BigDecimal overheadProgress;
    private BigDecimal materialEquivalent;
    private BigDecimal laborEquivalent;
    private BigDecimal overheadEquivalent;
    private BigDecimal periodMaterialCost;
    private BigDecimal periodLaborCost;
    private BigDecimal periodOverheadCost;
    private BigDecimal currentMaterialCost;
    private BigDecimal currentLaborCost;
    private BigDecimal currentOverheadCost;
    private BigDecimal completedMaterialCost;
    private BigDecimal completedLaborCost;
    private BigDecimal completedOverheadCost;
    private BigDecimal completedTotalCost;
    private BigDecimal completedUnitCost;
    private BigDecimal wipMaterialCost;
    private BigDecimal wipLaborCost;
    private BigDecimal wipOverheadCost;
    private BigDecimal wipTotalCost;
    private LocalDateTime calculateTime;
    /** 状态: 0=pending, 1=completed */
    private Integer status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
