package com.duoduo.jxc.dto.sales;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SalesOrderDTO {
    private Long orderId;
    private String docNo;
    private Integer orderType; // 1:批发单, 2:零售单, 3:销售预订单
    private LocalDate docDate;
    private Long storeId;
    private Long customerId;
    private Long operatorId;
    private Integer status;
    private Integer settleStatus;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal otherFee;
    private BigDecimal actualAmount;
    private BigDecimal paidAmount;
    private String remark;

    private List<SalesOrderDetailDTO> details;
}
