package com.duoduo.jxc.dto.wage;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PiecePriceDTO {
    private Long priceId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private String processCode;
    private String processName;
    private Long employeeId;
    private String employeeName;
    private BigDecimal unitPrice;
    private String unit;
    private Integer priceType;
    private Long batchId;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
    private Integer status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
