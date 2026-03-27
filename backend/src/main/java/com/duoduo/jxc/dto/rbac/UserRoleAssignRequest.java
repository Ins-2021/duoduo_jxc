package com.duoduo.jxc.dto.rbac;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleAssignRequest {

    @NotEmpty
    private List<Long> roleIds;
}

