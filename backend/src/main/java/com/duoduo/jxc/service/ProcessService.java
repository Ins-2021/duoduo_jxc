package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ProcessDTO;
import com.duoduo.jxc.dto.ProcessQuery;
import com.duoduo.jxc.entity.Process;

public interface ProcessService extends IService<Process> {
    PageResult<ProcessDTO> pageQuery(ProcessQuery query);
    ProcessDTO getDetail(Long id);
    Long create(ProcessDTO dto);
    void update(ProcessDTO dto);
    void delete(Long id);
}
