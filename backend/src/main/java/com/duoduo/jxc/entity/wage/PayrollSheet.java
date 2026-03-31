package com.duoduo.jxc.entity.wage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.duoduo.jxc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工资单主表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_payroll_sheet")
public class PayrollSheet extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long sheetId;
    private String sheetNo;
    /** 核算月份 YYYY-MM */
    @TableField("`year_month`")
    private String yearMonth;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long departmentId;
    private String departmentName;
    /** 发放人数 */
    private Integer employeeCount;
    private BigDecimal totalBaseWage;
    private BigDecimal totalPieceWage;
    private BigDecimal totalHourWage;
    private BigDecimal totalOvertimeWage;
    private BigDecimal totalBonus;
    private BigDecimal totalAllowance;
    /** 应发合计 */
    private BigDecimal totalPayable;
    private BigDecimal totalPersonalTax;
    private BigDecimal totalSocialInsurance;
    private BigDecimal totalHousingFund;
    private BigDecimal totalOtherDeduction;
    /** 扣款合计 */
    private BigDecimal totalDeduction;
    /** 实发合计 */
    private BigDecimal totalActual;
    /** 状态: 0-草稿/1-待审核/2-已审核/3-已发放 */
    private Integer status;
    private LocalDateTime submitTime;
    private Long submitBy;
    private LocalDateTime auditTime;
    private Long auditBy;
    private LocalDateTime payTime;
    private Long payBy;
    /** 发放方式: 0-银行/1-现金/2-混合 */
    private Integer payMethod;
    private String remark;
}
