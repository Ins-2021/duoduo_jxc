package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应收账款DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class ReceivableDTO {
    /**
     * 应收ID
     */
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
     * 状态：0-待收款，1-部分收款，2-已收款
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
     * 创建人
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
