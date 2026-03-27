package com.duoduo.jxc.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSkuSelectDTO {
    private Long id;
    private Long spuId;
    private String name;
    private String attributes;
    private String spec;
    private String brand;
    private String unit;
    private BigDecimal wholesalePrice;
    private BigDecimal purchasePrice;
    private BigDecimal retailPrice;
    private Integer stock;
    private String category;
    private String barcode;
    private String productCode;
    private String defaultWarehouse;
    private String location;
    private Integer exchangePoints;
    private String stockUnit;
    private BigDecimal stockValue;
    private String remark;
}
