package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 裁床单
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_cutting_plan")
public class CutOrder {

    @TableId(type = IdType.AUTO)
    private Long planId;
    private String planNo;
    private Long orderId;
    private Long fabricId;
    private Long markerId;
    private Integer plannedQuantity;
    private Integer cutQuantity;
    private LocalDate cuttingDate;
    private Long cuttingGroupId;
    private String cuttingMachine;
    /** 铺布方式: one_way/round_trip/folded */
    private String spreadMethod;
    private BigDecimal fabricConsumption;
    private BigDecimal fabricDefectRate;
    private BigDecimal utilizationRate;
    private BigDecimal actualEfficiency;
    private Integer cutPieces;
    /** 状态: pending/in_progress/completed/cancelled */
    private String status;
    private String remark;
    private Long createBy;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
}
