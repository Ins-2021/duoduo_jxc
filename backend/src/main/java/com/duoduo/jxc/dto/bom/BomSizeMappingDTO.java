package com.duoduo.jxc.dto.bom;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BomSizeMappingDTO {
    private Long id;
    private Long bomId;
    private Long bomItemId;
    private String productSize;
    private Long actualMaterialId;
    private BigDecimal actualUsage;
}
