package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FinanceAccountDTO {
    private Long accountId;
    private String accountName;
    private BigDecimal balance;
    private Integer status;
}
