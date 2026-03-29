package com.duoduo.jxc.service.auth.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.dto.auth.TokenResponse;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.service.auth.AuthTokenService;
import com.duoduo.jxc.service.rbac.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final SysUserMapper sysUserMapper;
    private final SysPermissionService sysPermissionService;

    private static final Duration ACCESS_TTL = Duration.ofHours(2);
    private static final Duration REFRESH_TTL = Duration.ofDays(7);

    @Override
    public TokenResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, 0)
                .eq(SysUser::getUsername, username)
                .last("limit 1"));
        if (user == null) {
            throw new BusinessException(BizCode.USER_NOT_FOUND);
        }
        List<String> perms = sysPermissionService.getUserPerms(user);
        IssuedAccessToken access = issueAccessToken(user.getUserId(), user.getUsername());
        String refresh = issueRefreshToken(user.getUserId(), user.getUsername());
        saveOnlineSession(user.getUserId(), user.getUsername(), access.jti, access.expiresAt, refresh, perms);

        TokenResponse resp = new TokenResponse();
        resp.setTokenType("Bearer");
        resp.setAccessToken(access.token);
        resp.setExpiresIn(ACCESS_TTL.toSeconds());
        resp.setRefreshToken(refresh);
        return resp;
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(BizCode.TOKEN_EMPTY);
        }
        String key = "auth:refresh:" + refreshToken;
        String raw = stringRedisTemplate.opsForValue().get(key);
        if (raw == null || raw.isBlank()) {
            throw new BusinessException(BizCode.TOKEN_INVALID);
        }
        RefreshPayload payload = JSON.parseObject(raw, RefreshPayload.class);
        SysUser user = sysUserMapper.selectById(payload.userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(BizCode.USER_NOT_FOUND);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(BizCode.USER_DISABLED);
        }
        List<String> perms = sysPermissionService.getUserPerms(user);
        IssuedAccessToken access = issueAccessToken(user.getUserId(), user.getUsername());
        saveOnlineSession(user.getUserId(), user.getUsername(), access.jti, access.expiresAt, refreshToken, perms);

        TokenResponse resp = new TokenResponse();
        resp.setTokenType("Bearer");
        resp.setAccessToken(access.token);
        resp.setExpiresIn(ACCESS_TTL.toSeconds());
        resp.setRefreshToken(refreshToken);
        return resp;
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            stringRedisTemplate.delete("auth:refresh:" + refreshToken);
        }
        if (accessToken == null || accessToken.isBlank()) {
            return;
        }
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(accessToken);
        } catch (Exception e) {
            return;
        }
        Long userId = jwt.getClaim("userId");
        if (userId != null) {
            stringRedisTemplate.delete("auth:online:user:" + userId);
            // 清理权限缓存
            stringRedisTemplate.delete("auth:perms:user:" + userId);
        }
        String jti = jwt.getId();
        Instant exp = jwt.getExpiresAt();
        if (jti == null || jti.isBlank() || exp == null) {
            return;
        }
        Duration ttl = Duration.between(Instant.now(), exp);
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        stringRedisTemplate.opsForValue().set("auth:blacklist:jti:" + jti, "1", ttl);
    }

    private IssuedAccessToken issueAccessToken(Long userId, String username) {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();
        Instant exp = now.plus(ACCESS_TTL);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("duoduo-jxc")
                .issuedAt(now)
                .expiresAt(exp)
                .subject(username)
                .id(jti)
                .claim("userId", userId)
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new IssuedAccessToken(token, jti, exp);
    }

    private String issueRefreshToken(Long userId, String username) {
        String token = UUID.randomUUID().toString().replace("-", "");
        RefreshPayload payload = new RefreshPayload();
        payload.userId = userId;
        payload.username = username;
        stringRedisTemplate.opsForValue().set("auth:refresh:" + token, JSON.toJSONString(payload), REFRESH_TTL);
        return token;
    }

    private void saveOnlineSession(Long userId, String username, String jti, Instant expiresAt, String refreshToken, List<String> perms) {
        if (userId == null || !StringUtils.hasText(username) || !StringUtils.hasText(jti) || expiresAt == null) {
            return;
        }
        Instant now = Instant.now();
        OnlineSession session = new OnlineSession();
        session.userId = userId;
        session.username = username;
        session.jti = jti;
        session.loginAt = now.toEpochMilli();
        session.lastSeenAt = now.toEpochMilli();
        session.expiresAt = expiresAt.toEpochMilli();
        session.refreshToken = refreshToken;
        session.perms = perms;
        Duration ttl = Duration.between(now, expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        stringRedisTemplate.opsForValue().set("auth:online:user:" + userId, JSON.toJSONString(session), ttl);
        // 同时将权限单独缓存，方便快速获取
        stringRedisTemplate.opsForValue().set("auth:perms:user:" + userId, JSON.toJSONString(perms), ttl);
    }

    public void kickoutByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        String key = "auth:online:user:" + userId;
        String raw = stringRedisTemplate.opsForValue().get(key);
        if (raw == null || raw.isBlank()) {
            return;
        }
        OnlineSession session = JSON.parseObject(raw, OnlineSession.class);
        if (session == null) {
            stringRedisTemplate.delete(key);
            return;
        }
        if (session.refreshToken != null && !session.refreshToken.isBlank()) {
            stringRedisTemplate.delete("auth:refresh:" + session.refreshToken);
        }
        if (session.jti != null && !session.jti.isBlank() && session.expiresAt > 0) {
            Instant exp = Instant.ofEpochMilli(session.expiresAt);
            Duration ttl = Duration.between(Instant.now(), exp);
            if (!ttl.isNegative() && !ttl.isZero()) {
                stringRedisTemplate.opsForValue().set("auth:blacklist:jti:" + session.jti, "1", ttl);
            }
        }
        stringRedisTemplate.delete(key);
        // 清理权限缓存
        stringRedisTemplate.delete("auth:perms:user:" + userId);
    }

    private static class RefreshPayload {
        public Long userId;
        public String username;
    }

    private static class IssuedAccessToken {
        public final String token;
        public final String jti;
        public final Instant expiresAt;

        private IssuedAccessToken(String token, String jti, Instant expiresAt) {
            this.token = token;
            this.jti = jti;
            this.expiresAt = expiresAt;
        }
    }

    private static class OnlineSession {
        public Long userId;
        public String username;
        public String jti;
        public long loginAt;
        public long lastSeenAt;
        public long expiresAt;
        public String refreshToken;
        public List<String> perms;
    }
}
