package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InventoryAgeDTO {
    private Long spuId;
    private String spuName;
    private String spuCode;
    private BigDecimal quantity;
    private BigDecimal amount;
    private LocalDate firstInDate;
    private Integer ageDays;
    private String ageRange;
    private String riskLevel;
}
