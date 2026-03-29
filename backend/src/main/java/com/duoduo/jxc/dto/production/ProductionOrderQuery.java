package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductionOrderQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String orderNo;
    private String styleNo;
    private Long styleId;
    private Long customerId;
    private Long factoryId;
    /** 状态: pending/producing/completed/cancelled */
    private String status;
    /** 优先级 */
    private String priority;
    private LocalDate deadlineFrom;
    private LocalDate deadlineTo;
}
