package com.duoduo.jxc.dto.product;

import lombok.Data;

@Data
public class ProductAttributeValueDTO {
    private Long id;
    private Long attributeId;
    private String value;
    private Integer sort;
}
