package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产计划
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_production_plan")
public class ProductionPlan {

    @TableId(type = IdType.AUTO)
    private Long planId;
    private String planNo;
    private Long orderId;
    private LocalDate plannedDate;
    private Integer plannedQuantity;
    private Integer actualQuantity;
    private Long cuttingGroupId;
    private Long sewingGroupId;
    /** 状态: draft/confirmed/in_progress/completed/cancelled */
    private String status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
}
