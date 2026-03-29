package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.ProductionScheduleDTO;
import com.duoduo.jxc.dto.production.ProductionScheduleQuery;
import com.duoduo.jxc.entity.ProductionSchedule;

public interface ProductionScheduleService extends IService<ProductionSchedule> {
    PageResult<ProductionScheduleDTO> pageQuery(ProductionScheduleQuery query);
    ProductionScheduleDTO getDetail(Long id);
    Long create(ProductionScheduleDTO dto);
    void update(ProductionScheduleDTO dto);
    void delete(Long id);
    void updateStatus(Long id, String status);
}
