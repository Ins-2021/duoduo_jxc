package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.settings.SystemSettingsDTO;
import com.duoduo.jxc.entity.SysConfig;

public interface SysConfigService extends IService<SysConfig> {

    SystemSettingsDTO getSystemSettings();

    void saveSystemSettings(SystemSettingsDTO dto);
}
