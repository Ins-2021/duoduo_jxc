package com.duoduo.jxc.security;

import com.alibaba.fastjson2.JSON;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtRedisAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final StringRedisTemplate stringRedisTemplate;

    public JwtRedisAuthoritiesConverter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object userIdObj = jwt.getClaim("userId");
        if (userIdObj == null) {
            return Collections.emptyList();
        }
        
        Long userId;
        if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;
        } else if (userIdObj instanceof Integer) {
            userId = ((Integer) userIdObj).longValue();
        } else {
            try {
                userId = Long.valueOf(userIdObj.toString());
            } catch (NumberFormatException e) {
                return Collections.emptyList();
            }
        }

        String key = "auth:perms:user:" + userId;
        String permsJson = stringRedisTemplate.opsForValue().get(key);

        if (permsJson == null || permsJson.isBlank()) {
            return Collections.emptyList();
        }

        try {
            List<String> perms = JSON.parseArray(permsJson, String.class);
            if (perms == null || perms.isEmpty()) {
                return Collections.emptyList();
            }
            return perms.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
