package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.ProductCostDTO;
import com.duoduo.jxc.dto.cost.ProductCostQuery;
import com.duoduo.jxc.entity.cost.ProductCost;

public interface ProductCostService extends IService<ProductCost> {

    PageResult<ProductCostDTO> pageQuery(ProductCostQuery query);

    ProductCostDTO getDetail(Long productCostId);

    Long create(ProductCostDTO dto);

    void update(ProductCostDTO dto);

    void delete(Long productCostId);
}
