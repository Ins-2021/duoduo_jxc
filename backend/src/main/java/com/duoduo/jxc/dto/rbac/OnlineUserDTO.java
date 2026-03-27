package com.duoduo.jxc.dto.rbac;

import lombok.Data;

@Data
public class OnlineUserDTO {
    private Long userId;
    private String username;
    private String jti;
    private Long loginAt;
    private Long lastSeenAt;
    private Long expiresAt;
}

