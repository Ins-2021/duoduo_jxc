package com.duoduo.jxc.dto.purchase;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrderDetailQuery extends PageQuery {
    private String docNo;
    private Long supplierId;
    private Long spuId;
    private Long skuId;
    private Integer orderType;
}
