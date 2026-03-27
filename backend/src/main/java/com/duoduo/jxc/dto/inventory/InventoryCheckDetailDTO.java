package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryCheckDetailDTO {
    private Long detailId;
    private Long checkId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String attr1;
    private String attr2;
    private Integer systemQty;
    private Integer actualQty;
    private Integer diffQty;
    private BigDecimal costPrice;
    private BigDecimal diffAmount;
}
