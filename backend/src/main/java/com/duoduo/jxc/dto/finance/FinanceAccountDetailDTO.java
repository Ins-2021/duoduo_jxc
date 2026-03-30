package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 财务账户详情DTO
 */
@Data
public class FinanceAccountDetailDTO {
    private Long accountId;
    private String accountName;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 统计信息
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private Long transactionCount;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    
    // 最近交易记录
    private List<FinanceTransactionDTO> recentTransactions;
}
