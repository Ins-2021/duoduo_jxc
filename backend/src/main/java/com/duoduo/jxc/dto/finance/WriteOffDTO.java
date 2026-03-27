package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 核销单DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class WriteOffDTO {
    /**
     * 核销ID
     */
    private Long writeOffId;
    
    /**
     * 核销单号
     */
    private String writeOffNo;
    
    /**
     * 类型：receivable-应收核销，payable-应付核销
     */
    private String type;
    
    /**
     * 单据编号
     */
    private String billNo;
    
    /**
     * 单据ID
     */
    private Long billId;
    
    /**
     * 收付款ID
     */
    private Long receiptPaymentId;
    
    /**
     * 核销金额
     */
    private BigDecimal amount;
    
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
