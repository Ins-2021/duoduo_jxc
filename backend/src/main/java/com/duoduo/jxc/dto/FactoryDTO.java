package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class FactoryDTO {
    private Long factoryId;
    private String name;
    private String code;
    private String factoryType;
    private String address;
    private String contact;
    private String phone;
    private Integer isActive;
    private String remark;
}
