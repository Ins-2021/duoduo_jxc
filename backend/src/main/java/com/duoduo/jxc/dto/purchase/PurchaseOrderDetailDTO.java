package com.duoduo.jxc.dto.purchase;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseOrderDetailDTO {
    private Long detailId;
    private Long orderId;
    private Long skuId;
    private Long spuId;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal lineAmount;
    private Long warehouseId;
}
