package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 账户收支统计DTO
 */
@Data
public class AccountStatisticsDTO {
    private Long accountId;
    private String accountName;
    private String startDate;
    private String endDate;
    
    // 收入统计
    private BigDecimal incomeAmount;
    private Integer incomeCount;
    
    // 支出统计
    private BigDecimal expenseAmount;
    private Integer expenseCount;
    
    // 净收支
    private BigDecimal netAmount;
    
    // 期初余额
    private BigDecimal openingBalance;
    
    // 期末余额
    private BigDecimal closingBalance;
}
