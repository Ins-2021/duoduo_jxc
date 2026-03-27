package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 零售日结单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_retail_settlement")
public class RetailSettlement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long settlementId;

    private String settlementNo;

    private Long storeId;

    private LocalDate settlementDate;

    private BigDecimal cashAmount;

    private BigDecimal wechatAmount;

    private BigDecimal alipayAmount;

    private BigDecimal cardAmount;

    private BigDecimal otherAmount;

    private BigDecimal totalAmount;

    private Integer orderCount;

    private Long operatorId;

    /**
     * 状态 1-正常 0-作废
     */
    private Integer status;
}