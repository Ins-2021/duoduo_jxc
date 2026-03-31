package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class QualityCheckDTO {
    private Long checkId;
    private String checkNo;
    private Long bundleId;
    private Long processId;
    private String result;
    private Integer checkQuantity;
    private Integer qualifiedQuantity;
    private Integer defectiveQuantity;
    private Long inspectorId;
    private java.time.LocalDateTime checkTime;
    private String remark;
}
