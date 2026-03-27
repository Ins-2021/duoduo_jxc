package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品 SKU (Stock Keeping Unit) 实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_product_sku")
public class ProductSku extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * SKU ID
     */
    @TableId(type = IdType.AUTO)
    private Long skuId;

    /**
     * 关联的 SPU ID
     */
    private Long spuId;

    /**
     * SKU 编码/条形码 (全局唯一)
     */
    private String skuCode;

    /**
     * 属性 1 (如：颜色-黑色)
     */
    private String attr1;

    /**
     * 属性 2 (如：尺码-XL)
     */
    private String attr2;

    /**
     * 进货价 (单位：分，或者 Decimal)
     */
    private BigDecimal purchasePrice;

    /**
     * 零售价 (单位：分，或者 Decimal)
     */
    private BigDecimal retailPrice;

    /**
     * 批发价 (单位：分，或者 Decimal)
     */
    private BigDecimal wholesalePrice;

    /**
     * 重量 (单位：克)
     */
    private BigDecimal weight;

    private Integer warningQty;

    /**
     * 状态 (0:停用, 1:启用)
     */
    private Integer status;
}
