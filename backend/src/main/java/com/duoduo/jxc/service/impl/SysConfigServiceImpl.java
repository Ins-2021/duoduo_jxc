package com.duoduo.jxc.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.dto.settings.SystemSettingsDTO;
import com.duoduo.jxc.entity.SysConfig;
import com.duoduo.jxc.mapper.SysConfigMapper;
import com.duoduo.jxc.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final String KEY_SYSTEM_SETTINGS = "systemSettings";

    @Override
    public SystemSettingsDTO getSystemSettings() {
        SysConfig config = getOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, KEY_SYSTEM_SETTINGS).last("limit 1"));
        if (config == null || !StringUtils.hasText(config.getConfigValue())) {
            return new SystemSettingsDTO();
        }
        try {
            return JSON.parseObject(config.getConfigValue(), SystemSettingsDTO.class);
        } catch (Exception e) {
            return new SystemSettingsDTO();
        }
    }

    @Override
    public void saveSystemSettings(SystemSettingsDTO dto) {
        if (dto == null) {
            return;
        }
        SysConfig existing = getOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, KEY_SYSTEM_SETTINGS).last("limit 1"));
        if (existing == null || !StringUtils.hasText(existing.getConfigValue())) {
            String jsonValue = JSON.toJSONString(dto);
            upsert(KEY_SYSTEM_SETTINGS, jsonValue, "系统参数配置");
            return;
        }
        try {
            var oldMap = JSON.parseObject(existing.getConfigValue(), new TypeReference<java.util.Map<String, Object>>() {});
            var newMap = JSON.parseObject(JSON.toJSONString(dto), new TypeReference<java.util.Map<String, Object>>() {});
            if (oldMap != null && newMap != null) {
                oldMap.putAll(newMap);
                String jsonValue = JSON.toJSONString(oldMap);
                upsert(KEY_SYSTEM_SETTINGS, jsonValue, "系统参数配置");
                return;
            }
        } catch (Exception ignored) {
        }
        String jsonValue = JSON.toJSONString(dto);
        upsert(KEY_SYSTEM_SETTINGS, jsonValue, "系统参数配置");
    }

    private void upsert(String key, String value, String remark) {
        SysConfig existing = getOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key).last("limit 1"));
        if (existing == null) {
            SysConfig config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setRemark(remark);
            save(config);
            return;
        }
        existing.setConfigValue(value);
        existing.setRemark(remark);
        updateById(existing);
    }
}
