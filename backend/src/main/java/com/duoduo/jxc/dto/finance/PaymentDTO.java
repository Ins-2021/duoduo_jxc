package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 付款单DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class PaymentDTO {
    /**
     * 付款ID
     */
    private Long paymentId;
    
    /**
     * 付款单号
     */
    private String paymentNo;
    
    /**
     * 供应商ID
     */
    private Long supplierId;
    
    /**
     * 供应商名称
     */
    private String supplierName;
    
    /**
     * 账户ID
     */
    private Long accountId;
    
    /**
     * 账户名称
     */
    private String accountName;
    
    /**
     * 付款金额
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
     * 付款日期
     */
    private LocalDateTime paymentDate;
    
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
