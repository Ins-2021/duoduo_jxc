package com.duoduo.jxc.dto.rbac;

import lombok.Data;

@Data
public class RoleUpdateRequest {

    private String roleName;

    private String dataScope;

    private Integer status;
}

