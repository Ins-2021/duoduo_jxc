package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class ColorDTO {
    private Long colorId;
    private String name;
    private String code;
    private String colorValue;
    private String pantoneNo;
    private Integer sortOrder;
    private Integer isActive;
}
