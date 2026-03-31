package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("jxc_wage_sheet")
public class PayrollPeriod {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 工资单号 */
    private String sheetNo;
    /** 年月 */
    @TableField("`year_month`")
    private String yearMonth;
    /** 起始日期 */
    private LocalDate startDate;
    /** 结束日期 */
    private LocalDate endDate;
    /** 员工人数 */
    private Integer employeeCount;
    /** 基本工资合计 */
    private BigDecimal totalBaseWage;
    /** 计件工资合计 */
    private BigDecimal totalPieceWage;
    /** 计时工资合计 */
    private BigDecimal totalHourWage;
    /** 加班工资合计 */
    private BigDecimal totalOvertimeWage;
    /** 奖金合计 */
    private BigDecimal totalBonus;
    /** 津贴合计 */
    private BigDecimal totalAllowance;
    /** 应发合计 */
    private BigDecimal totalPayable;
    /** 个人所得税合计 */
    private BigDecimal totalPersonalTax;
    /** 社保合计 */
    private BigDecimal totalSocialInsurance;
    /** 公积金合计 */
    private BigDecimal totalHousingFund;
    /** 其他扣款合计 */
    private BigDecimal totalOtherDeduction;
    /** 扣款合计 */
    private BigDecimal totalDeduction;
    /** 实发合计 */
    private BigDecimal totalActual;
    /** 状态: 0=draft, 1=pending, 2=approved, 3=paid */
    private Integer status;
    /** 提交时间 */
    private LocalDateTime submitTime;
    /** 提交人 */
    private Long submitBy;
    /** 审核时间 */
    private LocalDateTime auditTime;
    /** 审核人 */
    private Long auditBy;
    /** 发放时间 */
    private LocalDateTime payTime;
    /** 发放人 */
    private Long payBy;
    /** 发放方式 */
    private String payMethod;
    /** 备注 */
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
