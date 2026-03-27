package com.duoduo.jxc.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WfBindingSaveRequest {

    @NotBlank
    private String processDefKey;

    private Integer enabled;

    private String startCondition;
}

