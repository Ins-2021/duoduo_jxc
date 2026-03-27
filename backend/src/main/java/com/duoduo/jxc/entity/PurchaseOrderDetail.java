package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_purchase_order_detail")
public class PurchaseOrderDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long detailId;
    private Long orderId;
    private Long skuId;
    private Long spuId;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
    private Long warehouseId;
}
