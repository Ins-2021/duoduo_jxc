package com.duoduo.jxc.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BundleFlowDTO {
    private Long bundleId;
    private String bundleNo;
    private String status;
    private List<BundleProcessFlowDTO> processes;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;

    @Data
    public static class BundleProcessFlowDTO {
        private Long processId;
        private String processCode;
        private String processName;
        private String status;
        private LocalDateTime completeTime;
        private Long completeWorkerId;
        private String completeWorkerName;
    }
}
