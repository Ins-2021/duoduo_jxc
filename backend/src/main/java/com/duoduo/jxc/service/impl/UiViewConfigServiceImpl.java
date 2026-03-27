package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.UiViewConfig;
import com.duoduo.jxc.mapper.UiViewConfigMapper;
import com.duoduo.jxc.service.UiViewConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UiViewConfigServiceImpl extends ServiceImpl<UiViewConfigMapper, UiViewConfig> implements UiViewConfigService {

    @Override
    public List<UiViewConfig> listViews(String keyword) {
        LambdaQueryWrapper<UiViewConfig> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(UiViewConfig::getViewName, keyword).or().like(UiViewConfig::getViewKey, keyword));
        }
        wrapper.orderByAsc(UiViewConfig::getViewName).orderByAsc(UiViewConfig::getScene);
        return list(wrapper);
    }

    @Override
    public UiViewConfig getByViewKey(String viewKey) {
        if (!StringUtils.hasText(viewKey)) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<UiViewConfig>().eq(UiViewConfig::getViewKey, viewKey).last("limit 1"));
    }

    @Override
    public void saveConfig(String viewKey, String configJson) {
        UiViewConfig existing = getByViewKey(viewKey);
        if (existing == null) {
            UiViewConfig config = new UiViewConfig();
            config.setViewKey(viewKey);
            config.setViewName(viewKey);
            config.setScene("list");
            config.setConfigJson(configJson);
            config.setDefaultJson(configJson);
            config.setVersion(1);
            save(config);
            return;
        }
        existing.setConfigJson(configJson);
        existing.setVersion(existing.getVersion() == null ? 1 : existing.getVersion() + 1);
        updateById(existing);
    }

    @Override
    public void resetToDefault(String viewKey) {
        UiViewConfig existing = getByViewKey(viewKey);
        if (existing == null) {
            return;
        }
        existing.setConfigJson(existing.getDefaultJson());
        existing.setVersion(existing.getVersion() == null ? 1 : existing.getVersion() + 1);
        updateById(existing);
    }
}

