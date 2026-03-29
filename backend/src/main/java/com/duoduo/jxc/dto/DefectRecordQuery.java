package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DefectRecordQuery extends PageQuery {
    private String defectNo;
    private Long qualityCheckId;
    private String defectType;
    private String handlingMethod;
    private Long handledBy;
}
