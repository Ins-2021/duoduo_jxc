package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class QualityStandardDTO {
    private Long standardId;
    private String standardName;
    private String standardType;
    private String checkItems;
    private String passStandard;
    private Long orderId;
    private Long processId;
    private Integer isActive;
}
