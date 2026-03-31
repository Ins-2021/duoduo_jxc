package com.duoduo.jxc.dto.sales;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRechargeDTO {
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
    private String createTime;
}
