package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.auth.LoginRequest;
import com.duoduo.jxc.dto.auth.RefreshTokenRequest;
import com.duoduo.jxc.dto.auth.TokenResponse;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.security.SecurityUtils;
import com.duoduo.jxc.service.auth.AuthTokenService;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenService authTokenService;
    private final JWKSet jwkSet;
    private final SysUserMapper sysUserMapper;

    @PostMapping("/login")
    public Result<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return Result.success(authTokenService.login(request.getUsername(), request.getPassword()));
    }

    /**
     * 微信小程序登录
     */
    @PostMapping("/login/wechat")
    public Result<TokenResponse> loginByWechat(@RequestBody Map<String, String> params) {
        // TODO: 接入微信小程序登录，此处先用默认账号模拟
        // 实际需要调用微信 code2session 接口获取 openid
        return Result.success(authTokenService.login("worker", "123456"));
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user/info")
    public Result<Map<String, Object>> getUserInfo() {
        Long userId = SecurityUtils.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        Map<String, Object> info = new HashMap<>();
        if (user != null) {
            info.put("id", user.getUserId());
            info.put("name", user.getRealName());
            info.put("username", user.getUsername());
            info.put("departmentName", "");
        }
        return Result.success(info);
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
