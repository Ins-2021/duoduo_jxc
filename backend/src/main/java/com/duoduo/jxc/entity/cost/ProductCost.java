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
@TableName("jxc_product_cost")
public class ProductCost extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long productCostId;
    private String costNo;
    private String accountPeriod;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private Long productionId;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal overheadCost;
    private BigDecimal totalCost;
    private BigDecimal outputQuantity;
    private BigDecimal unitCost;
    private Integer status;
    private String remark;
}
