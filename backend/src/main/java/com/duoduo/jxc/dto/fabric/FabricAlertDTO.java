package com.duoduo.jxc.dto.fabric;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FabricAlertDTO {
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private String alertType;
    private String message;
    private BigDecimal currentQty;
    private BigDecimal thresholdQty;
}
