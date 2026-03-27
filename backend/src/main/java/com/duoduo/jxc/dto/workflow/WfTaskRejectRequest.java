package com.duoduo.jxc.dto.workflow;

import lombok.Data;

@Data
public class WfTaskRejectRequest {

    private Long userId;

    private String comment;

    private String rejectType;
}
