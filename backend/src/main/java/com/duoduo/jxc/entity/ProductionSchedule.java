package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产排程
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_production_schedule")
public class ProductionSchedule {

    @TableId(type = IdType.AUTO)
    private Long scheduleId;
    private String scheduleNo;
    private Long orderId;
    private Long planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String scheduleItems;
    /** 状态: draft/confirmed/in_progress/completed/cancelled */
    private String status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
}
