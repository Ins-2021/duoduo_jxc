package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QualityCheckQuery extends PageQuery {
    private String checkNo;
    private Long bundleId;
    private Long processId;
    private String result;
    private Long checkerId;
}
