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
@TableName("jxc_cost_pool")
public class CostPool extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long poolId;
    private String poolNo;
    private String accountPeriod;
    private Integer costType;
    private Long costCenterId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private Long productionId;
    private BigDecimal totalAmount;
    private BigDecimal allocatedAmount;
    private BigDecimal remainingAmount;
    private Integer status;
    private String remark;
}
