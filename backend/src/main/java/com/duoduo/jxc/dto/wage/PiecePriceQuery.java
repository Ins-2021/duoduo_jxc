package com.duoduo.jxc.dto.wage;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PiecePriceQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long styleId;
    private String styleCode;
    private String processCode;
    private Long employeeId;
    private Integer priceType;
    private Integer status;
    private LocalDate effectiveDateFrom;
    private LocalDate effectiveDateTo;
}
