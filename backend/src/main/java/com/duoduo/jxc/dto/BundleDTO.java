package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class BundleDTO {
    private Long bundleId;
    private String bundleNo;
    private Long orderId;
    private Long currentProcessId;
    private String status;
}
