package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class StyleColorwayDTO {
    private Long colorwayId;
    private Long styleId;
    private String colorwayNo;
    private String colorwayName;
    private String mainColor;
    private String accentColor;
    private String colorImages;
    private Integer isActive;
}
