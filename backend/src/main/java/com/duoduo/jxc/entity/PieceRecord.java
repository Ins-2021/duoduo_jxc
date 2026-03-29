package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 计件记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_piece_record")
public class PieceRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    /** 计件记录号 */
    private String recordNo;
    /** 记录日期 */
    private LocalDate recordDate;
    /** 生产订单ID */
    private Long productionId;
    /** 生产订单号 */
    private String productionNo;
    /** 款式ID */
    private Long styleId;
    /** 款式编码 */
    private String styleCode;
    /** 款式名称 */
    private String styleName;
    /** 工序编码 */
    private String processCode;
    /** 工序名称 */
    private String processName;
    /** 员工ID */
    private Long employeeId;
    /** 员工编号 */
    private String employeeCode;
    /** 员工姓名 */
    private String employeeName;
    /** 数量 */
    private BigDecimal quantity;
    /** 合格数量 */
    private BigDecimal qualifiedQuantity;
    /** 次品数量 */
    private BigDecimal defectQuantity;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 工资金额 */
    private BigDecimal wageAmount;
    /** 车间ID */
    private Long workshopId;
    /** 车间名称 */
    private String workshopName;
    /** 审核状态: 0=pending, 1=approved, 2=rejected */
    private Integer auditStatus;
    /** 审核时间 */
    private LocalDateTime auditTime;
    /** 审核人 */
    private Long auditBy;
    /** 审核备注 */
    private String auditRemark;
    /** 备注 */
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
