package com.duoduo.jxc.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatMiniLoginRequest {

    @NotBlank
    private String code;
}

