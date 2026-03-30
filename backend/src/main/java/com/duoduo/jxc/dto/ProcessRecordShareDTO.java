package com.duoduo.jxc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessRecordShareDTO {
    private Long shareId;
    private Long recordId;
    private Long workerId;
    private BigDecimal shareRatio;
    private BigDecimal amount;
}
