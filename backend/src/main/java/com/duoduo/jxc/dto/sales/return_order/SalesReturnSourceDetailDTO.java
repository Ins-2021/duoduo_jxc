package com.duoduo.jxc.dto.sales.return_order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesReturnSourceDetailDTO {
    private Long detailId;
    private Long skuId;
    private Long spuId;
    private String productName;
    private String productInfo;
    private Integer qty;
    private Integer returnedQty;
    private Integer availableQty;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
    private Long warehouseId;
    private String remark;
}
