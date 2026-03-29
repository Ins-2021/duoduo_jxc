package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeOptionDTO;
import com.duoduo.jxc.entity.SizeOption;

public interface SizeOptionService extends IService<SizeOption> {
    
    PageResult<SizeOptionDTO> pageQuery(PageQuery query);
    
    SizeOptionDTO getDetail(Long id);
    
    Long create(SizeOptionDTO dto);
    
    void update(SizeOptionDTO dto);
    
    void delete(Long id);
}
