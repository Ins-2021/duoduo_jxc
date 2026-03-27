package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应付账款DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class PayableDTO {
    /**
     * 应付ID
     */
    private Long payableId;
    
    /**
     * 单据编号
     */
    private String billNo;
    
    /**
     * 供应商ID
     */
    private Long supplierId;
    
    /**
     * 供应商名称
     */
    private String supplierName;
    
    /**
     * 应付金额
     */
    private BigDecimal amount;
    
    /**
     * 已付金额
     */
    private BigDecimal paidAmount;
    
    /**
     * 剩余金额
     */
    private BigDecimal remainingAmount;
    
    /**
     * 状态：0-待付款，1-部分付款，2-已付款
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
