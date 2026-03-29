package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class SizeRatioTemplateDTO {
    private Long templateId;
    private String name;
    private Long categoryId;
    private String ratios;
    private String description;
}
