package com.duoduo.jxc.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScanRecordDTO {
    private Long recordId;
    private Long bundleId;
    private String bundleNo;
    private Long processId;
    private String processName;
    private Long workerId;
    private String workerName;
    private Long workGroupId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String scanType;
    private String qrContent;
    private String deviceInfo;
    private String ipAddress;
    private String status;
    private LocalDateTime scanAt;
    private LocalDateTime confirmAt;
}
