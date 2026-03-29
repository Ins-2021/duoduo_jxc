package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QualityStandardQuery extends PageQuery {
    private String standardName;
    private String standardType;
    private Long orderId;
    private Long processId;
}
