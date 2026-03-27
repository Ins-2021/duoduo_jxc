package com.duoduo.jxc.controller;

import com.alibaba.fastjson2.JSON;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.rbac.OnlineUserDTO;
import com.duoduo.jxc.service.auth.impl.AuthTokenServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/system/online-users")
@RequiredArgsConstructor
public class SystemOnlineUserController {

    private final StringRedisTemplate stringRedisTemplate;
    private final AuthTokenServiceImpl authTokenService;

    @GetMapping
    @PreAuthorize("@perm.has('system:user:view')")
    public Result<List<OnlineUserDTO>> list() {
        Set<String> keys = stringRedisTemplate.keys("auth:online:user:*");
        if (keys == null || keys.isEmpty()) {
            return Result.success(List.of());
        }
        List<OnlineUserDTO> rows = new ArrayList<>();
        for (String key : keys) {
            String raw = stringRedisTemplate.opsForValue().get(key);
            if (raw == null || raw.isBlank()) {
                continue;
            }
            try {
                OnlineUserDTO dto = JSON.parseObject(raw, OnlineUserDTO.class);
                if (dto != null) {
                    rows.add(dto);
                }
            } catch (Exception ignored) {
            }
        }
        return Result.success(rows);
    }

    @PostMapping("/{userId}/kickout")
    @PreAuthorize("@perm.has('system:user:edit')")
    public Result<Void> kickout(@PathVariable Long userId) {
        authTokenService.kickoutByUserId(userId);
        return Result.success();
    }
}

