package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 裁床扎包
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_cutting_bundle")
public class CutBundle {

    @TableId(type = IdType.AUTO)
    private Long bundleId;
    private String bundleNo;
    private Long cuttingPlanId;
    private String size;
    private String color;
    private Integer quantity;
    private Integer bundleQuantity;
    private Integer maxPerBundle;
    private Long processId;
    /** 状态: pending/assigned/in_progress/completed/abnormal */
    private String status;
    private String qrCode;
    private String remark;
    private LocalDateTime createTime;
}
