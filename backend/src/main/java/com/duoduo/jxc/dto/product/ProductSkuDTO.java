package com.duoduo.jxc.dto.product;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class ProductSkuDTO {
    private Long skuId;

    private Long spuId;

    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;

    private String attr1;

    private String attr2;

    private BigDecimal purchasePrice;

    private BigDecimal retailPrice;

    private BigDecimal wholesalePrice;

    private BigDecimal weight;

    private Integer warningQty;

    private Integer status;
}
