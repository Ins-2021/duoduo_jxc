package com.duoduo.jxc.dto.wage;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PayrollPeriodDTO {
    private Long id;
    private String sheetNo;
    private String yearMonth;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer employeeCount;
    private BigDecimal totalBaseWage;
    private BigDecimal totalPieceWage;
    private BigDecimal totalHourWage;
    private BigDecimal totalOvertimeWage;
    private BigDecimal totalBonus;
    private BigDecimal totalAllowance;
    private BigDecimal totalPayable;
    private BigDecimal totalPersonalTax;
    private BigDecimal totalSocialInsurance;
    private BigDecimal totalHousingFund;
    private BigDecimal totalOtherDeduction;
    private BigDecimal totalDeduction;
    private BigDecimal totalActual;
    /** 状态: 0=draft, 1=pending, 2=approved, 3=paid */
    private Integer status;
    private LocalDateTime submitTime;
    private Long submitBy;
    private LocalDateTime auditTime;
    private Long auditBy;
    private LocalDateTime payTime;
    private Long payBy;
    private String payMethod;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
