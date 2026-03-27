package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.settings.SystemSettingsDTO;
import com.duoduo.jxc.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SysConfigService sysConfigService;

    @Log(title = "设置管理", action = "获取系统设置")
    @GetMapping
    @PreAuthorize("@perm.has('settings:system-params:view')")
    public Result<SystemSettingsDTO> getSettings() {
        return Result.success(sysConfigService.getSystemSettings());
    }

    @Log(title = "设置管理", action = "保存系统设置")
    @PutMapping
    @PreAuthorize("@perm.has('settings:system-params:edit')")
    public Result<Void> saveSettings(@RequestBody SystemSettingsDTO dto) {
        sysConfigService.saveSystemSettings(dto);
        return Result.success();
    }
}
