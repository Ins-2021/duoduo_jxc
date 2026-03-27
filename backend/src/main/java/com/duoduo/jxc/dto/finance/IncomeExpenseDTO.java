package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收支单DTO
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Data
public class IncomeExpenseDTO {
    /**
     * 收支ID
     */
    private Long ieId;
    
    /**
     * 收支单号
     */
    private String ieNo;
    
    /**
     * 类型：1-收入，2-支出
     */
    private Integer type;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 账户ID
     */
    private Long accountId;
    
    /**
     * 账户名称
     */
    private String accountName;
    
    /**
     * 金额
     */
    private BigDecimal amount;
    
    /**
     * 单据类型
     */
    private String billType;
    
    /**
     * 单据编号
     */
    private String billNo;
    
    /**
     * 状态：0-待审核，1-已审核
     */
    private Integer status;
    
    /**
     * 单据日期
     */
    private LocalDateTime billDate;
    
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
