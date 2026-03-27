package com.duoduo.jxc.service;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.product.ProductSkuSelectDTO;
import com.duoduo.jxc.dto.product.ProductSkuSelectQuery;

public interface ProductSelectService {
    PageResult<ProductSkuSelectDTO> pageSku(ProductSkuSelectQuery query);
}

