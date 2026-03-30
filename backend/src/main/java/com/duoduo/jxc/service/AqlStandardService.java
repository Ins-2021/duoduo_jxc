package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.AqlResultDTO;
import com.duoduo.jxc.dto.AqlStandardDTO;
import com.duoduo.jxc.dto.AqlStandardQuery;
import com.duoduo.jxc.entity.AqlStandard;

public interface AqlStandardService extends IService<AqlStandard> {
    PageResult<AqlStandardDTO> pageQuery(AqlStandardQuery query);
    AqlStandardDTO getDetail(Long id);
    Long create(AqlStandardDTO dto);
    void update(AqlStandardDTO dto);
    void delete(Long id);
    AqlResultDTO calculateSampleSize(Integer batchSize, String aqlLevel);
}
