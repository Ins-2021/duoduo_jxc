package com.duoduo.jxc.dto.finance;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 余额调整请求DTO
 */
@Data
public class BalanceAdjustRequest {
    private BigDecimal amount;
    private String remark;
}
