package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产订单
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_production_order")
public class ProductionOrder {

    @TableId(type = IdType.AUTO)
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
