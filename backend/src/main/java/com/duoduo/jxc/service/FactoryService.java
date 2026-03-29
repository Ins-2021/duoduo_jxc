package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.FactoryDTO;
import com.duoduo.jxc.entity.Factory;

public interface FactoryService extends IService<Factory> {
    
    PageResult<FactoryDTO> pageQuery(PageQuery query);
    
    FactoryDTO getDetail(Long id);
    
    Long create(FactoryDTO dto);
    
    void update(FactoryDTO dto);
    
    void delete(Long id);
}
