package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 应收来源明细实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_receivable_source")
public class ReceivableSource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应收单ID
     */
    private Long receivableId;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 关联订单明细ID
     */
    private Long detailId;

    /**
     * 商品SKU ID
     */
    private Long skuId;

    /**
     * 数量
     */
    private Integer qty;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 金额
     */
    private BigDecimal lineAmount;
}
