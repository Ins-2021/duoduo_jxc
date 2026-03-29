package com.duoduo.jxc.dto.cost;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StandardCostQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long styleId;
    private String styleCode;
    /** 是否当前版本: 0=no, 1=yes */
    private Integer isCurrent;
    private String versionNo;
    private LocalDate effectiveDateFrom;
    private LocalDate effectiveDateTo;
}
