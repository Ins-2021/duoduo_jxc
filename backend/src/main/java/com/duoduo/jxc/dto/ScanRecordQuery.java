package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScanRecordQuery extends PageQuery {
    private Long workerId;
    private Long processId;
    private Long bundleId;
    private String bundleNo;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
