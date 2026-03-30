package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatrolCheckQuery extends PageQuery {
    private String patrolNo;
    private Long workshopId;
    private Long inspectorId;
    private String checkType;
    private String result;
}
