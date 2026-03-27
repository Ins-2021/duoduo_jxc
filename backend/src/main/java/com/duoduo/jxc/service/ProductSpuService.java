package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.product.ProductSpuDTO;
import com.duoduo.jxc.dto.product.ProductSpuQuery;
import com.duoduo.jxc.entity.ProductSpu;

public interface ProductSpuService extends IService<ProductSpu> {

    PageResult<ProductSpuDTO> pageQuery(ProductSpuQuery query);

    ProductSpuDTO getDetail(Long spuId);

    Long saveProduct(ProductSpuDTO dto);

    void updateProduct(ProductSpuDTO dto);

    void deleteProduct(Long spuId);
}
