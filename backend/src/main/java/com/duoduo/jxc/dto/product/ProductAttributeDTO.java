package com.duoduo.jxc.dto.product;

import lombok.Data;
import java.util.List;

@Data
public class ProductAttributeDTO {
    private Long id;
    private String name;
    private Integer sort;
    private List<ProductAttributeValueDTO> options;
}
