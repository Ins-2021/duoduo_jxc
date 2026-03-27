package com.duoduo.jxc.dto.sales;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SalesOrderQuery extends PageQuery {
    private String docNo;
    private Integer orderType;
    private Integer status;
    private Long customerId;
}
