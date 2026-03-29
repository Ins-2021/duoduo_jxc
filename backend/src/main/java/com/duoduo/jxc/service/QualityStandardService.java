package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.QualityStandardDTO;
import com.duoduo.jxc.dto.QualityStandardQuery;
import com.duoduo.jxc.entity.QualityStandard;

public interface QualityStandardService extends IService<QualityStandard> {
    PageResult<QualityStandardDTO> pageQuery(QualityStandardQuery query);
    QualityStandardDTO getDetail(Long id);
    Long create(QualityStandardDTO dto);
    void update(QualityStandardDTO dto);
    void delete(Long id);
}
