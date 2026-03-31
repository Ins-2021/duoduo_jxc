package com.duoduo.jxc.dto.product;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 商品SPU数据传输对象
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Data
public class ProductSpuDTO {
    private Long spuId;

    @NotBlank(message = "SPU名称不能为空")
    private String spuName;

    private String productCode;
    
    private String spec;
    
    private String imageUrls;

    private Long categoryId;
    
    private String categoryName;

    private String unit;

    private Long brandId;
    
    private String brandName;
    
    private java.math.BigDecimal purchasePrice;
    
    private java.math.BigDecimal retailPrice;
    
    private java.math.BigDecimal wholesalePrice;
    
    private Integer exchangePoints;
    
    private Long defaultWarehouseId;
    
    private String productLocation;
    
    private Integer warningQty;
    
    private String remark;

    private Integer status;
    
    // 初始库存数量，用于新增商品时同时生成期初库存
    private Integer initialStockQty;
    
    // 当前库存
    private Integer currentStock;
    
    // 期初成本价
    private java.math.BigDecimal initialCostPrice;
    
    // 期初总金额
    private java.math.BigDecimal initialTotalAmount;
    
    private String attr1Name;
    private String attr2Name;

    /**
     * 关联款式ID
     */
    private Long styleId;

    /**
     * 款式编号（用于显示）
     */
    private String styleNo;

    /**
     * 款式名称（用于显示）
     */
    private String styleName;

    private List<ProductSkuDTO> skuList;
}
