package com.duoduo.jxc.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WfModelSaveRequest {

    @NotBlank
    private String bpmnXml;

    private String name;

    private String category;
}

