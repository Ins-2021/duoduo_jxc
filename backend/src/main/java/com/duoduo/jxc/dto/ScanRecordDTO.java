package com.duoduo.jxc.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScanRecordDTO {
    private String bundleNo;
    private Long processId;
    private Long workerId;
    private Integer quantity;
}
