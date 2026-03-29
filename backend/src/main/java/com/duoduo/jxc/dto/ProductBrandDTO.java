package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class ProductBrandDTO {
    private Long brandId;
    private String brandName;
    private Integer sort;
    private Integer status;
}
