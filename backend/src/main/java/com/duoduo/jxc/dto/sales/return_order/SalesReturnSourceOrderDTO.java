package com.duoduo.jxc.dto.sales.return_order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SalesReturnSourceOrderDTO {
    private Long orderId;
    private String docNo;
    private LocalDate docDate;
    private Long customerId;
    private String customerName;
    private BigDecimal actualAmount;
    private List<SalesReturnSourceDetailDTO> details;
}
