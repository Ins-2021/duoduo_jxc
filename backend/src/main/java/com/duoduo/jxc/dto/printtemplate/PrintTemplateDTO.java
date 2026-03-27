package com.duoduo.jxc.dto.printtemplate;

import lombok.Data;

@Data
public class PrintTemplateDTO {
    private Long templateId;
    private String templateName;
    private String bizType;
    private String paperType;
    private Integer paperWidthMm;
    private Integer paperHeightMm;
    private String designJson;
    private Integer isDefault;
    private Integer enabled;
}

