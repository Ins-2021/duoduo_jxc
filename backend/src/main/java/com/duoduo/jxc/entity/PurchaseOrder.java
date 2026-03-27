package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_purchase_order")
public class PurchaseOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long orderId;
    private String docNo;
    private Integer orderType;
    private LocalDate docDate;
    private Long supplierId;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private BigDecimal otherFee;
    private BigDecimal actualAmount;
    private String remark;
}
