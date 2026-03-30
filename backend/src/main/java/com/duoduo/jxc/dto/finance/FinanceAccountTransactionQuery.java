package com.duoduo.jxc.dto.finance;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户交易查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FinanceAccountTransactionQuery extends PageQuery {
    private Integer type;
    private String startDate;
    private String endDate;
    private String keyword;
}
