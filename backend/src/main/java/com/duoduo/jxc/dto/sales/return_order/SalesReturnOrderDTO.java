package com.duoduo.jxc.dto.sales.return_order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SalesReturnOrderDTO {
    private Long orderId;
    private String docNo;
    private LocalDate docDate;
    private Long customerId;
    private String customerName;
    private Long originSalesId;
    private Integer status;
    private BigDecimal refundAmount;
    private String refundMethod;
    private String remark;

    private List<SalesReturnDetailDTO> details;
}
