package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class WorkGroupDTO {
    private Long groupId;
    private String name;
    private String code;
    private Long factoryId;
    private Long workshopId;
    private Long leaderId;
    private String groupType;
    private Integer isActive;
    private String remark;
}
