package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工资单明细表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_wage_sheet_detail")
public class PayrollSheetDetail {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 工资单ID */
    private Long sheetId;
    /** 工资单号 */
    private String sheetNo;
    /** 年月 */
    private String yearMonth;
    /** 员工ID */
    private Long employeeId;
    /** 员工编号 */
    private String employeeCode;
    /** 员工姓名 */
    private String employeeName;
    /** 部门ID */
    private Long departmentId;
    /** 部门名称 */
    private String departmentName;
    /** 岗位名称 */
    private String positionName;
    /** 成本类型 */
    private String costType;
    /** 银行账号 */
    private String bankAccount;
    /** 银行名称 */
    private String bankName;
    /** 基本工资 */
    private BigDecimal baseWage;
    /** 计件工资 */
    private BigDecimal pieceWage;
    /** 计时工资 */
    private BigDecimal hourWage;
    /** 加班工资 */
    private BigDecimal overtimeWage;
    /** 绩效奖金 */
    private BigDecimal performanceBonus;
    /** 工龄工资 */
    private BigDecimal seniorityWage;
    /** 餐补 */
    private BigDecimal mealAllowance;
    /** 交通补贴 */
    private BigDecimal transportAllowance;
    /** 通讯补贴 */
    private BigDecimal communicationAllowance;
    /** 其他补贴 */
    private BigDecimal otherAllowance;
    /** 奖金合计 */
    private BigDecimal bonus;
    /** 津贴合计 */
    private BigDecimal allowance;
    /** 应发合计 */
    private BigDecimal totalPayable;
    /** 个人所得税 */
    private BigDecimal personalTax;
    /** 社保 */
    private BigDecimal socialInsurance;
    /** 公积金 */
    private BigDecimal housingFund;
    /** 考勤扣款 */
    private BigDecimal attendanceDeduction;
    /** 其他扣款 */
    private BigDecimal otherDeduction;
    /** 扣款合计 */
    private BigDecimal totalDeduction;
    /** 实发合计 */
    private BigDecimal totalActual;
    /** 计件数量 */
    private Integer pieceCount;
    /** 工时 */
    private BigDecimal workHours;
    /** 加班工时 */
    private BigDecimal overtimeHours;
    /** 工作天数 */
    private Integer workDays;
    /** 发放状态: 0=pending, 1=paid, 2=failed */
    private Integer payStatus;
    /** 发放时间 */
    private LocalDateTime payTime;
    /** 发放方式 */
    private String payMethod;
    /** 备注 */
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
