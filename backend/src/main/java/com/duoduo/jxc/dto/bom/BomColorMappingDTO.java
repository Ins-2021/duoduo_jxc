package com.duoduo.jxc.dto.bom;

import lombok.Data;

@Data
public class BomColorMappingDTO {
    private Long id;
    private Long bomId;
    private Long bomItemId;
    private String productColor;
    private Long actualMaterialId;
    private String actualMaterialColor;
}
