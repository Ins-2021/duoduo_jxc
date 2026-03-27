package com.duoduo.jxc.dto.rbac;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long deptId;

    private String realName;

    private Integer status;
}

