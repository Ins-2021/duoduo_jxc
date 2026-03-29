package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class ProcessDTO {
    private Long processId;
    private String processCode;
    private String processName;
    private String processType;
    private java.math.BigDecimal standardPrice;
    private Integer status;
}
