package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReworkOrderQuery extends PageQuery {
    private String reworkNo;
    private Long checkId;
    private Long bundleId;
    private String status;
}
