package com.duoduo.jxc.dto.fabric;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FabricDTO {
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private String fabricType;
    private String composition;
    private String width;
    private String weight;
    private String color;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Long supplierId;
    private BigDecimal minStock;
    private BigDecimal maxStock;
    private Integer status;
    private String remark;
}
