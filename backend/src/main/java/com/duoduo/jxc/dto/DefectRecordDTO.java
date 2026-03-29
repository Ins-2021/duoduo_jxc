package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class DefectRecordDTO {
    private Long defectId;
    private String defectNo;
    private Long qualityCheckId;
    private String defectType;
    private Integer quantity;
    private String handlingMethod;
    private String handlingRemark;
    private Long handledBy;
    private java.time.LocalDateTime handledAt;
}
