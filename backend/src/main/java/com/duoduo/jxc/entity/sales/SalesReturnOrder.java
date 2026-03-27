package com.duoduo.jxc.entity.sales;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_sales_return_order")
public class SalesReturnOrder extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long orderId;
    private String docNo;
    private LocalDate docDate;
    private Long customerId;
    private Long originSalesId;
    private Integer status;
    private BigDecimal refundAmount;
    private String refundMethod;
    private String remark;
}
