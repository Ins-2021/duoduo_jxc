package com.duoduo.jxc.dto.product;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuQuery extends PageQuery {
    private String spuName;
    private String productCode;
    private String spec;
    private Long categoryId;
    private Long brandId;
    private Integer status;
}
