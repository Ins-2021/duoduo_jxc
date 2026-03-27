package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收款单DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class ReceiptDTO {
    /**
     * 收款ID
     */
    private Long receiptId;
    
    /**
     * 收款单号
     */
    private String receiptNo;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户名称
     */
    private String customerName;
    
    /**
     * 账户ID
     */
    private Long accountId;
    
    /**
     * 账户名称
     */
    private String accountName;
    
    /**
     * 收款金额
     */
    private BigDecimal amount;
    
    /**
     * 支付方式
     */
    private String payMethod;
    
    /**
     * 状态：0-待确认，1-已完成
     */
    private Integer status;
    
    /**
     * 收款日期
     */
    private LocalDateTime receiptDate;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建人
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
