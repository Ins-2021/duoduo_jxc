package com.duoduo.jxc.dto.workflow;

import lombok.Data;

import java.util.Map;

@Data
public class WfTaskApproveRequest {

    private Long userId;

    private String comment;

    private Map<String, Object> variables;
}
