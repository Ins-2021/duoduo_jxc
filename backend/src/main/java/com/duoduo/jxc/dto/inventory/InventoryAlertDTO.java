package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryAlertDTO {
    private Long alertId;
    private Long warehouseId;
    private String warehouseName;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String attr1;
    private String attr2;
    private Integer currentQty;
    private Integer minQty;
    private Integer maxQty;
    private Integer alertType;
    private Integer status;
    private LocalDateTime alertTime;
}
