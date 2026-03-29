package com.duoduo.jxc.dto.production;

import lombok.Data;

@Data
public class CutBundleQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String bundleNo;
    private Long cuttingPlanId;
    private String size;
    private String color;
    /** 状态: pending/assigned/in_progress/completed/abnormal */
    private String status;
}
