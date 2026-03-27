package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 销售订单明细实体类 (扁平化 SKU 明细行)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sales_order_detail")
public class SalesOrderDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 明细 ID
     */
    @TableId(type = IdType.AUTO)
    private Long detailId;

    /**
     * 所属销售单 ID
     */
    private Long orderId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SPU ID (冗余查询)
     */
    private Long spuId;

    /**
     * 数量
     */
    private Integer qty;

    /**
     * 预定数量 (仅预订单有效)
     */
    private Integer bookedQty;

    /**
     * 未转销售数量 (追踪预定未发货量)
     */
    private Integer unfulfilledQty;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 单行折扣
     */
    private BigDecimal discountAmount;

    /**
     * 单行总金额 = 单价 * 数量 - 折扣
     */
    private BigDecimal lineAmount;

    /**
     * 库房 ID (支持单行选择出库仓库)
     */
    private Long warehouseId;

    /**
     * 备注
     */
    private String remark;
}
