package com.duoduo.jxc.entity.cost;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_cost_allocation")
public class CostAllocation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
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
