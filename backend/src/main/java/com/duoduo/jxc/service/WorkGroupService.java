package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.WorkGroupDTO;
import com.duoduo.jxc.entity.WorkGroup;

public interface WorkGroupService extends IService<WorkGroup> {
    
    PageResult<WorkGroupDTO> pageQuery(PageQuery query);
    
    WorkGroupDTO getDetail(Long id);
    
    Long create(WorkGroupDTO dto);
    
    void update(WorkGroupDTO dto);
    
    void delete(Long id);
}
