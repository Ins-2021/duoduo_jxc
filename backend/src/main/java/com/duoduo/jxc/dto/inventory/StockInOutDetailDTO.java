package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockInOutDetailDTO {
    private Long detailId;
    private Long inOutId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String attr1;
    private String attr2;
    private Integer qty;
    private BigDecimal costPrice;
    private BigDecimal amount;
}
