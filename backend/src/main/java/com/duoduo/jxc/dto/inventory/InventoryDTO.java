package com.duoduo.jxc.dto.inventory;

import lombok.Data;

@Data
public class InventoryDTO {
    private Long inventoryId;
    private Long warehouseId;
    private Long skuId;
    private Integer qty;
}
