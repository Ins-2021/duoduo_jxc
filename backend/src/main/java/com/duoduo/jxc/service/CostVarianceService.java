package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostVarianceDTO;
import com.duoduo.jxc.dto.cost.CostVarianceQuery;
import com.duoduo.jxc.entity.CostVariance;

public interface CostVarianceService extends IService<CostVariance> {
    PageResult<CostVarianceDTO> pageQuery(CostVarianceQuery query);
    CostVarianceDTO getDetail(Long id);
    Long create(CostVarianceDTO dto);
    void update(CostVarianceDTO dto);
    void delete(Long id);
    void analyze(Long id);
}
