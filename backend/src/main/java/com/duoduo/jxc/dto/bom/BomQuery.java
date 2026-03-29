package com.duoduo.jxc.dto.bom;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BomQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String bomNo;
    private Long styleId;
    private String styleCode;
    private Integer status;
    private LocalDate effectiveDateFrom;
    private LocalDate effectiveDateTo;
}
