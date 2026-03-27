package com.duoduo.jxc.dto.sales;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SalesOrderDetailQuery extends PageQuery {
    private String docNo;
    private Long customerId;
    private Long spuId;
    private Long skuId;
    private Integer orderType;
}
