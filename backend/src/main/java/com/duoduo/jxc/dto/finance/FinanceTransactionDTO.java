package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务流水DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class FinanceTransactionDTO {
    /**
     * 流水ID
     */
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
     * 类型：1-收入，2-支出，3-转账
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
     * 分类
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
