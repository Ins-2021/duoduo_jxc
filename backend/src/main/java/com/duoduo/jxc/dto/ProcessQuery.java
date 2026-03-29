package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessQuery extends PageQuery {
    private String processCode;
    private String processName;
    private String processType;
}
