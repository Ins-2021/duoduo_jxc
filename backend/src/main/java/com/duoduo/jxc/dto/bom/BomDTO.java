package com.duoduo.jxc.dto.bom;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BomDTO {
    private Long bomId;
    private String bomNo;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private String versionNo;
    private Integer status;
    private LocalDate effectiveDate;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<BomItemDTO> items;
    private List<BomProcessDTO> processes;
}
