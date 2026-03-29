package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_quotation")
public class Quotation extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long quotationId;
    private String quotationNo;
    private Long customerId;
    private String customerName;
    private String contactName;
    private String phone;
    private Long salesId;
    private String salesName;
    private java.time.LocalDate quotationDate;
    private java.time.LocalDate validUntil;
    private String currency;
    private java.math.BigDecimal totalAmount;
    private java.math.BigDecimal discountRate;
    private java.math.BigDecimal discountAmount;
    private java.math.BigDecimal finalAmount;
    private String quotationStatus;
    private String remark;
}
