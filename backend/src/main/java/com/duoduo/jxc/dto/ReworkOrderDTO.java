package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class ReworkOrderDTO {
    private Long reworkId;
    private String reworkNo;
    private Long checkId;
    private Long bundleId;
    private String status;
    private Long handlerId;
    private java.time.LocalDateTime handleTime;
    private String remark;
}
