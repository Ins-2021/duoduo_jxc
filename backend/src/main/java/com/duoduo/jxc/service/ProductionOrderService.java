package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.MaterialRequirementDTO;
import com.duoduo.jxc.dto.production.ProductionInboundDTO;
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

    /**
     * 计算生产订单的面料需求
     *
     * @param productionOrderId 生产订单ID
     * @return 面料需求计算结果
     */
    MaterialRequirementDTO calculateMaterialRequirement(Long productionOrderId);

    /**
     * 生产入库
     *
     * @param dto 生产入库请求
     */
    void productionInbound(ProductionInboundDTO dto);
}
