package com.duoduo.jxc.dto.wage;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayrollSheetDetailDTO {
    private Long id;
    private Long sheetId;
    private String sheetNo;
    private String yearMonth;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Long departmentId;
    private String departmentName;
    private String positionName;
    private String costType;
    private String bankAccount;
    private String bankName;
    private BigDecimal baseWage;
    private BigDecimal pieceWage;
    private BigDecimal hourWage;
    private BigDecimal overtimeWage;
    private BigDecimal performanceBonus;
    private BigDecimal seniorityWage;
    private BigDecimal mealAllowance;
    private BigDecimal transportAllowance;
    private BigDecimal communicationAllowance;
    private BigDecimal otherAllowance;
    private BigDecimal bonus;
    private BigDecimal allowance;
    private BigDecimal totalPayable;
    private BigDecimal personalTax;
    private BigDecimal socialInsurance;
    private BigDecimal housingFund;
    private BigDecimal attendanceDeduction;
    private BigDecimal otherDeduction;
    private BigDecimal totalDeduction;
    private BigDecimal totalActual;
    private Integer pieceCount;
    private BigDecimal workHours;
    private BigDecimal overtimeHours;
    private Integer workDays;
    /** 发放状态: 0=pending, 1=paid, 2=failed */
    private Integer payStatus;
    private LocalDateTime payTime;
    private String payMethod;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
