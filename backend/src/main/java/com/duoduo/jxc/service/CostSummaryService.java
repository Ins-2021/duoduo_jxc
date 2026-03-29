package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostSummaryDTO;
import com.duoduo.jxc.dto.cost.CostSummaryQuery;
import com.duoduo.jxc.entity.CostSummary;

public interface CostSummaryService extends IService<CostSummary> {
    PageResult<CostSummaryDTO> pageQuery(CostSummaryQuery query);
    CostSummaryDTO getDetail(Long id);
    Long create(CostSummaryDTO dto);
    void update(CostSummaryDTO dto);
    void delete(Long id);
}
