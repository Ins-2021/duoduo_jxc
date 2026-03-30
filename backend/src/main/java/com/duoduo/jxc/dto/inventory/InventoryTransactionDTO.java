package com.duoduo.jxc.dto.inventory;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryTransactionDTO {
    private Long transactionId;
    private Long warehouseId;
    private String warehouseName;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String spuName;
    private Integer transType;
    private String transTypeName;
    private Integer qty;
    private Integer beforeQty;
    private Integer afterQty;
    private String billType;
    private String billNo;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createTime;
}
