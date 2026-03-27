package com.duoduo.jxc.dto.auth;

import lombok.Data;

@Data
public class TokenResponse {

    private String tokenType;

    private String accessToken;

    private Long expiresIn;

    private String refreshToken;
}

