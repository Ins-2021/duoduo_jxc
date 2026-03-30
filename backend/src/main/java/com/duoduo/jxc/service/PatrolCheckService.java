package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.PatrolCheckDTO;
import com.duoduo.jxc.dto.PatrolCheckQuery;
import com.duoduo.jxc.entity.PatrolCheck;

public interface PatrolCheckService extends IService<PatrolCheck> {
    PageResult<PatrolCheckDTO> pageQuery(PatrolCheckQuery query);
    PatrolCheckDTO getDetail(Long id);
    Long create(PatrolCheckDTO dto);
    void update(PatrolCheckDTO dto);
    void delete(Long id);
}
