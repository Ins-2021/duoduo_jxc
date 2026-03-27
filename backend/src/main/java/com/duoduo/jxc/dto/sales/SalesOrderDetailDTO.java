package com.duoduo.jxc.dto.sales;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesOrderDetailDTO {
    private Long detailId;
    private Long orderId;
    private Long skuId;
    private Long spuId;
    private Integer qty;
    private Integer bookedQty;
    private Integer unfulfilledQty;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal lineAmount;
    private Long warehouseId;
    private String remark;

    // 商品信息（查询时填充，不入库）
    private String spuName;
    private String skuCode;
    private String attr1;
    private String attr2;
}
