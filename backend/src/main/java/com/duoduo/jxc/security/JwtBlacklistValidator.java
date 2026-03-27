package com.duoduo.jxc.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtBlacklistValidator implements OAuth2TokenValidator<Jwt> {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        OAuth2TokenValidatorResult base = JwtValidators.createDefault().validate(token);
        if (base.hasErrors()) {
            return base;
        }
        String jti = token.getId();
        if (jti == null || jti.isBlank()) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "missing jti", null));
        }
        String key = "auth:blacklist:jti:" + jti;
        String val = stringRedisTemplate.opsForValue().get(key);
        if (val != null) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "token revoked", null));
        }
        return OAuth2TokenValidatorResult.success();
    }
}
