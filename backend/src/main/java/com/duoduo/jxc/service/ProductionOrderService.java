package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.entity.ProductionOrder;

public interface ProductionOrderService extends IService<ProductionOrder> {
    PageResult<ProductionOrderDTO> pageQuery(ProductionOrderQuery query);
    ProductionOrderDTO getDetail(Long id);
    Long create(ProductionOrderDTO dto);
    void update(ProductionOrderDTO dto);
    void delete(Long id);
    void updateStatus(Long id, String status);
}
