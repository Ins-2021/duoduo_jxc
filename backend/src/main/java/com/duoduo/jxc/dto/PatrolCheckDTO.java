package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class PatrolCheckDTO {
    private Long patrolId;
    private String patrolNo;
    private Long workshopId;
    private Long inspectorId;
    private String checkType;
    private String result;
    private String remark;
    private java.time.LocalDateTime checkTime;
}
