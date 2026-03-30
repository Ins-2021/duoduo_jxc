package com.duoduo.jxc.dto.fabric;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FabricInboundDTO {
    private Long inboundId;
    private String inboundNo;
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private Long warehouseId;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private String batchNo;
    private Long supplierId;
    private Integer status;
    private String remark;
    private LocalDateTime inboundDate;
}
