package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.DefectRecordDTO;
import com.duoduo.jxc.dto.DefectRecordQuery;
import com.duoduo.jxc.entity.DefectRecord;

public interface DefectRecordService extends IService<DefectRecord> {
    PageResult<DefectRecordDTO> pageQuery(DefectRecordQuery query);
    DefectRecordDTO getDetail(Long id);
    Long create(DefectRecordDTO dto);
    void update(DefectRecordDTO dto);
    void delete(Long id);
}
