package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PieceRecordDTO;
import com.duoduo.jxc.dto.wage.PieceRecordQuery;
import com.duoduo.jxc.entity.PieceRecord;

import java.util.List;
import java.util.Map;

public interface PieceRecordService extends IService<PieceRecord> {
    PageResult<PieceRecordDTO> pageQuery(PieceRecordQuery query);
    PieceRecordDTO getDetail(Long id);
    Long create(PieceRecordDTO dto);
    void update(PieceRecordDTO dto);
    void delete(Long id);
    void audit(Long id, Integer auditStatus);
    List<Map<String, Object>> summary(PieceRecordQuery query);
}
