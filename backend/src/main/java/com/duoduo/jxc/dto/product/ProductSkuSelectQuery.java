package com.duoduo.jxc.dto.product;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSkuSelectQuery extends PageQuery {
    private String keyword;
    private Long categoryId;
    private List<Long> categoryIds;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean hasStock;
}

