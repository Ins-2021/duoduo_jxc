package com.duoduo.jxc.dto.purchase;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderDTO {
    private Long orderId;
    private String docNo;
    private Integer orderType;
    private LocalDate docDate;
    private Long supplierId;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private BigDecimal otherFee;
    private BigDecimal actualAmount;
    private String remark;
    
    private List<PurchaseOrderDetailDTO> details;
}
