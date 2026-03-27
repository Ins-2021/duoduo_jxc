package com.duoduo.jxc.dto.rbac;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    private String newPassword;
}

