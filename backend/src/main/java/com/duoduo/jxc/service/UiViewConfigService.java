package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.entity.UiViewConfig;

import java.util.List;

public interface UiViewConfigService extends IService<UiViewConfig> {

    List<UiViewConfig> listViews(String keyword);

    UiViewConfig getByViewKey(String viewKey);

    void saveConfig(String viewKey, String configJson);

    void resetToDefault(String viewKey);
}

