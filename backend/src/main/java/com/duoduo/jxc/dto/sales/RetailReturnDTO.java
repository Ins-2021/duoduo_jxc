package com.duoduo.jxc.dto.sales;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RetailReturnDTO {
    private Long returnId;
    private String docNo;
    private Long customerId;
    private String customerName;
    private LocalDate returnDate;
    private BigDecimal totalAmount;
    private Integer status;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private Long auditBy;
    private String auditByName;
    private String auditTime;
    private String createTime;
}
