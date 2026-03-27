package com.duoduo.jxc.dto.retail;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RetailSettlementDTO {
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
    private Integer status;
    private LocalDateTime createTime;
}