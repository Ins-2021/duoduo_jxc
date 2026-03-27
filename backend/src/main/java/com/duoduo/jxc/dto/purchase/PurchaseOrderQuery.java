package com.duoduo.jxc.dto.purchase;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrderQuery extends PageQuery {
    private String docNo;
    private Integer orderType;
    private Integer status;
    private Long supplierId;
}
