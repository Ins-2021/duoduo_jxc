package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应收单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_receivable")
public class Receivable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 应收单ID
     */
    @TableId(type = IdType.AUTO)
    private Long receivableId;

    /**
     * 单据编号
     */
    private String billNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 应收金额
     */
    private BigDecimal amount;

    /**
     * 已收金额
     */
    private BigDecimal receivedAmount;

    /**
     * 剩余金额
     */
    private BigDecimal remainingAmount;

    /**
     * 状态 0-未收款 1-部分核销 2-已核销
     */
    private Integer status;

    /**
     * 单据日期
     */
    private LocalDateTime billDate;

    /**
     * 到期日期
     */
    private LocalDateTime dueDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 来源单据类型
     */
    private String sourceType;

    /**
     * 来源单据ID
     */
    private Long sourceId;

    /**
     * 来源单据编号
     */
    private String sourceDocNo;
}
