package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ProcessRecordDTO;
import com.duoduo.jxc.dto.ProcessRecordQuery;
import com.duoduo.jxc.entity.ProcessRecord;

public interface ProcessRecordService extends IService<ProcessRecord> {
    PageResult<ProcessRecordDTO> pageQuery(ProcessRecordQuery query);
    ProcessRecordDTO getDetail(Long id);
    Long create(ProcessRecordDTO dto);
    void update(ProcessRecordDTO dto);
    void delete(Long id);
}
