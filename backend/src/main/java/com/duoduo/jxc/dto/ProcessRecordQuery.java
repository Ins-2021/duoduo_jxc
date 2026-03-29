package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessRecordQuery extends PageQuery {
    private Long workerId;
    private Long processId;
    private Long bundleId;
}
