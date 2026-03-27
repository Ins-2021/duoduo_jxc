package com.duoduo.jxc.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("perm")
public class Perm {

    public boolean has(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return false;
        }
        for (GrantedAuthority a : authorities) {
            if (a == null) {
                continue;
            }
            String v = a.getAuthority();
            if (v == null) {
                continue;
            }
            if ("*:*:*".equals(v) || "*".equals(v)) {
                return true;
            }
            if (permission.equals(v)) {
                return true;
            }
        }
        return false;
    }
}

