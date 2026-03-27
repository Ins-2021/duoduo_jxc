package com.duoduo.jxc.dto.rbac;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank
    private String username;

    private Long deptId;

    @NotBlank
    private String realName;

    @NotBlank(message = "密码不能为空")
    private String password;

    private Integer status;
}

