package com.duoduo.jxc.dto.sales.return_order;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SalesReturnOrderQuery extends PageQuery {
    private String docNo;
    private Long customerId;
    private Integer status;
}
