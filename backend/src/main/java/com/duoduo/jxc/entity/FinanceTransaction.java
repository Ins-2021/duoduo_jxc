package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务流水实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_finance_transaction")
public class FinanceTransaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 流水ID
     */
    @TableId(type = IdType.AUTO)
    private Long transactionId;

    /**
     * 流水号
     */
    private String transactionNo;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 类型 1-收入 2-支出 3-转账
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 类别
     */
    private String category;

    /**
     * 单据类型
     */
    private String billType;

    /**
     * 单据编号
     */
    private String billNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 交易日期
     */
    private LocalDateTime transactionDate;
}
