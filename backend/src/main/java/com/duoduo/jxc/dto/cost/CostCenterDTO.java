package com.duoduo.jxc.dto.cost;

import lombok.Data;

@Data
public class CostCenterDTO {
    private Long costCenterId;
    private String centerCode;
    private String centerName;
    private Integer centerType;
    private Long parentId;
    private Long managerId;
    private Integer status;
    private String remark;
}
