package com.duoduo.jxc.dto.inventory;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BatchDTO {
    private Long batchId;
    private String batchNo;
    private Long skuId;
    private Long warehouseId;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private LocalDate inboundDate;
    private Integer qty;
    private String remark;
}
