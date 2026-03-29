package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class StyleDTO {
    private Long styleId;
    private String styleNo;
    private String styleName;
    private Integer year;
    private String season;
    private String series;
    private Long categoryId;
    private Long brandId;
    private Long designerId;
    private String styleType;
    private String targetGender;
    private String targetAgeGroup;
    private String designImage;
    private String sampleImage;
    private String techPack;
    private Integer status;
}
