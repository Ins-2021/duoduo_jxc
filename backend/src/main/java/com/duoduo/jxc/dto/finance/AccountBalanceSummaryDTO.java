package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 账户余额汇总DTO
 */
@Data
public class AccountBalanceSummaryDTO {
    private BigDecimal totalBalance;
    private Integer accountCount;
    private List<AccountBalanceItemDTO> accounts;
    
    @Data
    public static class AccountBalanceItemDTO {
        private Long accountId;
        private String accountName;
        private BigDecimal balance;
        private String status;
    }
}
