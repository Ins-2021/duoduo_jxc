package com.duoduo.jxc.dto.sales;

import lombok.Data;

@Data
public class QuotationDetailDTO {
    private Long detailId;
    private Long quotationId;
    private Long styleId;
    private String styleCode;
    private String styleName;
    private Long skuId;
    private String color;
    private String size;
    private String unit;
    private Integer quantity;
    private java.math.BigDecimal unitPrice;
    private java.math.BigDecimal discountRate;
    private java.math.BigDecimal amount;
    private String remark;
    private Integer sortOrder;
}
