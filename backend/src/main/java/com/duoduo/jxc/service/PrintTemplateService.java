package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateDTO;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateQuery;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateSettingsDTO;
import com.duoduo.jxc.entity.PrintTemplate;

public interface PrintTemplateService extends IService<PrintTemplate> {
    PageResult<PrintTemplateDTO> pageQuery(PrintTemplateQuery query);
    PrintTemplateDTO getDetail(Long id);
    PrintTemplateDTO saveTemplate(PrintTemplateDTO dto);
    Long copyTemplate(Long id);
    void setDefault(Long id);
    void setEnabled(Long id, Integer enabled);
    PrintTemplateSettingsDTO getSettings();
    void saveSettings(PrintTemplateSettingsDTO dto);
    java.util.List<String> listBizTypes();
}
