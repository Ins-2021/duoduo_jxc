package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeRatioTemplateDTO;
import com.duoduo.jxc.entity.SizeRatioTemplate;

public interface SizeRatioTemplateService extends IService<SizeRatioTemplate> {
    
    PageResult<SizeRatioTemplateDTO> pageQuery(PageQuery query);
    
    SizeRatioTemplateDTO getDetail(Long id);
    
    Long create(SizeRatioTemplateDTO dto);
    
    void update(SizeRatioTemplateDTO dto);
    
    void delete(Long id);
}
