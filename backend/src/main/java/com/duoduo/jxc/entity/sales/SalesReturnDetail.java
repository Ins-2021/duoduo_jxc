package com.duoduo.jxc.entity.sales;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sales_return_detail")
public class SalesReturnDetail extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long detailId;
    private Long orderId;
    private Long originSalesDetailId;
    private Long skuId;
    private Long spuId;
    private Integer originQty;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
    private Long warehouseId;
    private String remark;
}
