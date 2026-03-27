package com.duoduo.jxc.dto.finance;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FinanceAccountQuery extends PageQuery {
    private String accountName;
}
