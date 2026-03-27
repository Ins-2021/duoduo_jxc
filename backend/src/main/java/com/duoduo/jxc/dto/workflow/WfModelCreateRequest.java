package com.duoduo.jxc.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WfModelCreateRequest {

    @NotBlank
    private String modelKey;

    @NotBlank
    private String name;

    private String category;
}

