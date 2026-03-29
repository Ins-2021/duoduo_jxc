package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostPeriodDTO;
import com.duoduo.jxc.dto.cost.CostPeriodQuery;
import com.duoduo.jxc.entity.CostPeriod;

public interface CostPeriodService extends IService<CostPeriod> {
    PageResult<CostPeriodDTO> pageQuery(CostPeriodQuery query);
    CostPeriodDTO getDetail(Long id);
    Long create(CostPeriodDTO dto);
    void update(CostPeriodDTO dto);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}
