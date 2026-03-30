package com.duoduo.jxc.dto.fabric;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FabricRequisitionDTO {
    private Long requisitionId;
    private String requisitionNo;
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private Long warehouseId;
    private BigDecimal quantity;
    private Long productionOrderId;
    private Long applicantId;
    private String purpose;
    private Integer status;
    private String approveComment;
    private LocalDateTime approveTime;
}
