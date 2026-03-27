package com.duoduo.jxc.dto.rbac;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleCreateRequest {

    @NotBlank
    private String roleKey;

    @NotBlank
    private String roleName;

    private String dataScope;

    private Integer status;
}

