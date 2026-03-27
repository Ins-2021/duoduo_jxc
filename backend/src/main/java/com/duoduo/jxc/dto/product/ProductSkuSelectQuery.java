package com.duoduo.jxc.dto.product;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSkuSelectQuery extends PageQuery {
    private String keyword;
    private Long categoryId;
}

