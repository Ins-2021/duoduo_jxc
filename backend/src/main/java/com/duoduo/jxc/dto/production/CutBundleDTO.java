package com.duoduo.jxc.dto.production;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CutBundleDTO {
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
