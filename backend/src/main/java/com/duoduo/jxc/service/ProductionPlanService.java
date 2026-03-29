package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.ProductionPlanDTO;
import com.duoduo.jxc.dto.production.ProductionPlanQuery;
import com.duoduo.jxc.entity.ProductionPlan;

public interface ProductionPlanService extends IService<ProductionPlan> {
    PageResult<ProductionPlanDTO> pageQuery(ProductionPlanQuery query);
    ProductionPlanDTO getDetail(Long id);
    Long create(ProductionPlanDTO dto);
    void update(ProductionPlanDTO dto);
    void delete(Long id);
    void updateStatus(Long id, String status);
}
