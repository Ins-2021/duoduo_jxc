package com.duoduo.jxc.dto.fabric;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FabricInventoryDTO {
    private Long inventoryId;
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal quantity;
    private BigDecimal availableQty;
    private BigDecimal lockedQty;
    private String batchNo;
    private BigDecimal costPrice;
}
