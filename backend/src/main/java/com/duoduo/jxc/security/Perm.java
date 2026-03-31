package com.duoduo.jxc.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("perm")
public class Perm {

    /**
     * 判断当前用户是否拥有指定权限
     * 管理员用户(admin)拥有所有权限
     */
    public boolean has(String permission) {
        // 管理员用户拥有所有权限
        if (isAdmin()) {
            return true;
        }

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
            if (permission.equals(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前用户是否是管理员
     */
    private boolean isAdmin() {
        String username = SecurityUtils.getUsername();
        return "admin".equalsIgnoreCase(username);
    }
}

