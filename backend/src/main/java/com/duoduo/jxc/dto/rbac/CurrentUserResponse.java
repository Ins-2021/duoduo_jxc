package com.duoduo.jxc.dto.rbac;

import lombok.Data;

import java.util.List;

@Data
public class CurrentUserResponse {

    private Long userId;

    private String username;

    private String realName;

    private List<String> perms;

    private List<String> roles;
}

