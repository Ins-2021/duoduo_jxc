package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_customer_recharge")
public class CustomerRecharge {
    @TableId(type = IdType.AUTO)
    private Long rechargeId;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private BigDecimal giftAmount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String payMethod;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createTime;
    private Integer deleted;
}
