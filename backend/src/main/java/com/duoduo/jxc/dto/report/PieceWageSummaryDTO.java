package com.duoduo.jxc.dto.report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class PieceWageSummaryDTO {
    private Long workerId;
    private String workerName;
    private String departmentName;
    private YearMonth reportMonth;
    private Integer recordCount;
    private BigDecimal totalQuantity;
    private BigDecimal totalAmount;
    private BigDecimal reworkAmount;
    private BigDecimal netAmount;
}
