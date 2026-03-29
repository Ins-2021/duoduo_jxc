package com.duoduo.jxc.dto.sales;

import lombok.Data;

@Data
public class QuotationDTO {
    private Long quotationId;
    private String quotationNo;
    private Long customerId;
    private String customerName;
    private String contactName;
    private String phone;
    private Long salesId;
    private String salesName;
    private java.time.LocalDate quotationDate;
    private java.time.LocalDate validUntil;
    private String currency;
    private java.math.BigDecimal totalAmount;
    private java.math.BigDecimal discountRate;
    private java.math.BigDecimal discountAmount;
    private java.math.BigDecimal finalAmount;
    private String quotationStatus;
    private String remark;
    private java.util.List<QuotationDetailDTO> details;
}
