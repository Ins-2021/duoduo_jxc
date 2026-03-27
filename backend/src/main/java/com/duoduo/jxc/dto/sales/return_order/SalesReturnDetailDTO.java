package com.duoduo.jxc.dto.sales.return_order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesReturnDetailDTO {
    private Long detailId;
    private Long orderId;
    private Long originSalesDetailId;
    private Long skuId;
    private Long spuId;
    private Integer originQty;
    private Integer returnedQty;
    private Integer availableQty;
    private String productName;
    private String productInfo;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
    private Long warehouseId;
    private String remark;
}
