package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class ProcessRecordDTO {
    private Long recordId;
    private Long workerId;
    private Long processId;
    private Long bundleId;
    private Integer quantity;
    private java.math.BigDecimal amount;
    private String status;
    private java.time.LocalDateTime recordTime;
}
