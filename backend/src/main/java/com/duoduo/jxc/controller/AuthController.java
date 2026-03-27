package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.auth.LoginRequest;
import com.duoduo.jxc.dto.auth.RefreshTokenRequest;
import com.duoduo.jxc.dto.auth.TokenResponse;
import com.duoduo.jxc.service.auth.AuthTokenService;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenService authTokenService;
    private final JWKSet jwkSet;

    @PostMapping("/login")
    public Result<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return Result.success(authTokenService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return Result.success(authTokenService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization,
                               @RequestParam(required = false) String refreshToken) {
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring("Bearer ".length()).trim();
        }
        authTokenService.logout(token, refreshToken);
        return Result.success();
    }

    @GetMapping("/jwks")
    public Map<String, Object> jwks() {
        return jwkSet.toJSONObject();
    }
}
