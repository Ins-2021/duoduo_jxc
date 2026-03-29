package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.BatchDTO;
import com.duoduo.jxc.dto.inventory.BatchQuery;
import com.duoduo.jxc.entity.Batch;

public interface BatchService extends IService<Batch> {
    PageResult<BatchDTO> pageQuery(BatchQuery query);
    BatchDTO getDetail(Long id);
    Long create(BatchDTO dto);
    void update(BatchDTO dto);
    void delete(Long id);
}
