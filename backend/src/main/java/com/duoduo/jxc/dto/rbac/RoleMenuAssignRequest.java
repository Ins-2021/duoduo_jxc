package com.duoduo.jxc.dto.rbac;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RoleMenuAssignRequest {

    @NotEmpty
    private List<Long> menuIds;
}

