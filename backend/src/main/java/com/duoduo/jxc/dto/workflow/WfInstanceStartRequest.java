package com.duoduo.jxc.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class WfInstanceStartRequest {

    @NotBlank
    private String bizType;

    @NotBlank
    private String bizId;

    @NotBlank
    private String title;

    private Long initiatorId;

    private Map<String, Object> variables;
}
