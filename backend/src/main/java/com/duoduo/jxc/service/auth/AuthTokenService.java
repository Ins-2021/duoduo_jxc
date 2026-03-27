package com.duoduo.jxc.service.auth;

import com.duoduo.jxc.dto.auth.TokenResponse;

public interface AuthTokenService {

    TokenResponse login(String username, String password);

    TokenResponse refresh(String refreshToken);

    void logout(String accessToken, String refreshToken);
}

