package com.duoduo.jxc.dto.oplog;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperLogQuery extends PageQuery {
    private String content;
    private Long operatorId;
    private String startDate;
    private String endDate;
}

