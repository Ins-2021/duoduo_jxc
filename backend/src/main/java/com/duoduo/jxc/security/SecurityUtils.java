package com.duoduo.jxc.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

    public static Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }

    public static Long getUserId() {
        Jwt jwt = getJwt();
        if (jwt == null) {
            return null;
        }
        Object v = jwt.getClaims().get("userId");
        if (v == null) {
            return null;
        }
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUsername() {
        Jwt jwt = getJwt();
        return jwt == null ? null : jwt.getSubject();
    }
}

