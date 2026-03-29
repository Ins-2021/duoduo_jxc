package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class SizeChartDTO {
    private Long chartId;
    private Long standardId;
    private Long categoryId;
    private String sizeCode;
    private java.math.BigDecimal bust;
    private java.math.BigDecimal waist;
    private java.math.BigDecimal hip;
    private java.math.BigDecimal shoulderWidth;
    private java.math.BigDecimal sleeveLength;
    private java.math.BigDecimal length;
    private java.math.BigDecimal inseam;
}
