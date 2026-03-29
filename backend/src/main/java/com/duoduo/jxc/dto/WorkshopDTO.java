package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class WorkshopDTO {
    private Long workshopId;
    private Long factoryId;
    private String name;
    private String code;
    private String workshopType;
    private Long managerId;
    private Integer isActive;
    private String remark;
}
