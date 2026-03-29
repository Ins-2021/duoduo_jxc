package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.WorkshopDTO;
import com.duoduo.jxc.entity.Workshop;

public interface WorkshopService extends IService<Workshop> {
    
    PageResult<WorkshopDTO> pageQuery(PageQuery query);
    
    WorkshopDTO getDetail(Long id);
    
    Long create(WorkshopDTO dto);
    
    void update(WorkshopDTO dto);
    
    void delete(Long id);
}
