package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 零售日结单明细实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_retail_settlement_order")
public class RetailSettlementOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long settlementId;

    private Long salesOrderId;

    private String salesOrderDocNo;

    private String paymentMethod;

    private BigDecimal amount;
}