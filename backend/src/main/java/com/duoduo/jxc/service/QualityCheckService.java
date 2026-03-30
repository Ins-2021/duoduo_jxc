package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.QualityCheckDTO;
import com.duoduo.jxc.dto.QualityCheckQuery;
import com.duoduo.jxc.dto.QualityCheckSubmitDTO;
import com.duoduo.jxc.entity.QualityCheck;

public interface QualityCheckService extends IService<QualityCheck> {
    PageResult<QualityCheckDTO> pageQuery(QualityCheckQuery query);
    QualityCheckDTO getDetail(Long id);
    Long create(QualityCheckDTO dto);
    void update(QualityCheckDTO dto);
    void delete(Long id);
    Long submitCheckResult(QualityCheckSubmitDTO dto);
}
