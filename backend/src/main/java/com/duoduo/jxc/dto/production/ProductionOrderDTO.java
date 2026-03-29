package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductionOrderDTO {
    private Long orderId;
    private String orderNo;
    private String styleNo;
    private String styleName;
    private Long styleId;
    private Long customerId;
    private Integer quantity;
    private Integer completedQuantity;
    private LocalDate deadline;
    /** 优先级: low/normal/high/urgent */
    private String priority;
    private Long factoryId;
    private Long sourceOrderId;
    /** 状态: pending/producing/completed/cancelled */
    private String status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
