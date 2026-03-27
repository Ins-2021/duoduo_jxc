package com.duoduo.jxc.service.impl;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.product.ProductSkuSelectDTO;
import com.duoduo.jxc.dto.product.ProductSkuSelectQuery;
import com.duoduo.jxc.mapper.ProductSelectMapper;
import com.duoduo.jxc.service.ProductSelectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSelectServiceImpl implements ProductSelectService {

    private final ProductSelectMapper productSelectMapper;

    @Override
    public PageResult<ProductSkuSelectDTO> pageSku(ProductSkuSelectQuery query) {
        int pageNum = query == null || query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query == null || query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        long offset = (long) (pageNum - 1) * pageSize;

        String keyword = query == null ? null : query.getKeyword();
        if (StringUtils.hasText(keyword)) {
            keyword = keyword.trim();
        }
        Long categoryId = query == null ? null : query.getCategoryId();

        long total = productSelectMapper.countSku(keyword, categoryId);
        if (total <= 0) {
            return new PageResult<>(0L, List.of());
        }

        List<ProductSkuSelectDTO> list = productSelectMapper.selectSkuPage(keyword, categoryId, offset, pageSize);
        list.forEach(i -> {
            if (i.getSpuId() != null) {
                i.setProductCode("SP" + String.format("%07d", i.getSpuId()));
            }
        });
        return new PageResult<>(Long.valueOf(total), list);
    }
}
