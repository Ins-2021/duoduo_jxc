package com.duoduo.jxc.dto.sales;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PointsExchangeDTO {
    private Long exchangeId;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private Integer points;
    private Integer quantity;
    private Integer status;
    private String remark;
    private Long operatorId;
    private String operatorName;
    private String exchangeTime;
    private String createTime;
}
