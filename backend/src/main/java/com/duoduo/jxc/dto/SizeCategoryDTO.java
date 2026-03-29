package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class SizeCategoryDTO {
    private Long categoryId;
    private String name;
    private String code;
    private String description;
    private Integer sortOrder;
    private Integer isActive;
}
