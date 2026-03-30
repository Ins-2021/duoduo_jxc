package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class AqlStandardDTO {
    private Long aqlId;
    private String level;
    private Integer batchSizeMin;
    private Integer batchSizeMax;
    private Integer sampleSize;
    private Integer acceptNum;
    private Integer rejectNum;
}
