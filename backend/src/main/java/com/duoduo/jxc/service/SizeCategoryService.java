package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeCategoryDTO;
import com.duoduo.jxc.entity.SizeCategory;

public interface SizeCategoryService extends IService<SizeCategory> {
    
    PageResult<SizeCategoryDTO> pageQuery(PageQuery query);
    
    SizeCategoryDTO getDetail(Long id);
    
    Long create(SizeCategoryDTO dto);
    
    void update(SizeCategoryDTO dto);
    
    void delete(Long id);
}
