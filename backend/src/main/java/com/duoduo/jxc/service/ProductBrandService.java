package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ProductBrandDTO;
import com.duoduo.jxc.entity.ProductBrand;

public interface ProductBrandService extends IService<ProductBrand> {
    
    PageResult<ProductBrandDTO> pageQuery(PageQuery query);
    
    ProductBrandDTO getDetail(Long id);
    
    Long create(ProductBrandDTO dto);
    
    void update(ProductBrandDTO dto);
    
    void delete(Long id);
}

