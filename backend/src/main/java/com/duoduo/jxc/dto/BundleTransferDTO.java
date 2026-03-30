package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class BundleTransferDTO {
    private Long bundleId;
    private Long fromWorkerId;
    private Long toWorkerId;
    private Long processId;
    private String remark;
}
