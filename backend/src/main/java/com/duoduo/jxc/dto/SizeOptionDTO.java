package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class SizeOptionDTO {
    private Long optionId;
    private Long categoryId;
    private String name;
    private String code;
    private Integer sortOrder;
}
