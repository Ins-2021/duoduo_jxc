package com.duoduo.jxc.controller;

import com.alibaba.fastjson2.JSON;
import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ui.UiViewConfigDTO;
import com.duoduo.jxc.dto.ui.UiViewConfigResetRequest;
import com.duoduo.jxc.dto.ui.UiViewConfigSaveRequest;
import com.duoduo.jxc.dto.ui.UiViewSummaryDTO;
import com.duoduo.jxc.entity.UiViewConfig;
import com.duoduo.jxc.service.UiViewConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ui")
@RequiredArgsConstructor
public class UiViewConfigController {

    private final UiViewConfigService uiViewConfigService;

    @Log(title = "设置", action = "查询视图列表")
    @GetMapping("/views")
    @PreAuthorize("@perm.has('settings:field-settings:view')")
    public Result<List<UiViewSummaryDTO>> listViews(@RequestParam(required = false) String keyword) {
        List<UiViewConfig> list = uiViewConfigService.listViews(keyword);
        List<UiViewSummaryDTO> dtoList = list.stream().map(item -> {
            UiViewSummaryDTO dto = new UiViewSummaryDTO();
            dto.setViewKey(item.getViewKey());
            dto.setViewName(item.getViewName());
            dto.setScene(item.getScene());
            dto.setVersion(item.getVersion());
            dto.setUpdateTime(item.getUpdateTime());
            return dto;
        }).toList();
        return Result.success(dtoList);
    }

    @Log(title = "设置", action = "查询视图配置")
    @GetMapping("/view-config")
    public Result<UiViewConfigDTO> getConfig(@RequestParam String viewKey) {
        if (!StringUtils.hasText(viewKey)) {
            return Result.error("viewKey不能为空");
        }
        UiViewConfig config = uiViewConfigService.getByViewKey(viewKey);
        if (config == null) {
            return Result.error("未找到对应视图配置");
        }
        UiViewConfigDTO dto = new UiViewConfigDTO();
        dto.setViewKey(config.getViewKey());
        dto.setViewName(config.getViewName());
        dto.setScene(config.getScene());
        dto.setVersion(config.getVersion());
        dto.setColumns(JSON.parse(config.getConfigJson()));
        return Result.success(dto);
    }

    @Log(title = "设置", action = "保存视图配置")
    @PutMapping("/view-config")
    @PreAuthorize("@perm.has('settings:field-settings:edit')")
    public Result<Void> saveConfig(@RequestBody UiViewConfigSaveRequest request) {
        if (request == null || !StringUtils.hasText(request.getViewKey())) {
            return Result.error("viewKey不能为空");
        }
        String configJson = JSON.toJSONString(request.getColumns());
        uiViewConfigService.saveConfig(request.getViewKey(), configJson);
        return Result.success();
    }

    @Log(title = "设置", action = "恢复视图默认配置")
    @PostMapping("/view-config/reset")
    @PreAuthorize("@perm.has('settings:field-settings:edit')")
    public Result<Void> reset(@RequestBody UiViewConfigResetRequest request) {
        if (request == null || !StringUtils.hasText(request.getViewKey())) {
            return Result.error("viewKey不能为空");
        }
        uiViewConfigService.resetToDefault(request.getViewKey());
        return Result.success();
    }
}
