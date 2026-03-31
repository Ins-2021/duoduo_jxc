package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品 SPU (Standard Product Unit) 实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_product_spu")
public class ProductSpu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * SPU ID
     */
    @TableId(type = IdType.AUTO)
    private Long spuId;

    /**
     * SPU 名称 (如：2024春季新款休闲裤)
     */
    private String spuName;

    /**
     * 商品编号
     */
    private String productCode;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 商品图像
     */
    private String imageUrls;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 单位 (如：件、条、双)
     */
    private String unit;

    /**
     * 品牌 ID
     */
    private Long brandId;

    /**
     * 进货价格
     */
    private java.math.BigDecimal purchasePrice;

    /**
     * 零售价格
     */
    private java.math.BigDecimal retailPrice;

    /**
     * 批发价格
     */
    private java.math.BigDecimal wholesalePrice;

    /**
     * 兑换积分
     */
    private Integer exchangePoints;

    /**
     * 默认仓库 ID
     */
    private Long defaultWarehouseId;

    /**
     * 商品货位
     */
    private String productLocation;

    /**
     * 库存预警
     */
    private Integer warningQty;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 属性1名称(如颜色)
     */
    private String attr1Name;

    /**
     * 属性2名称(如尺码)
     */
    private String attr2Name;

    /**
     * 状态 (0:下架, 1:上架)
     */
    private Integer status;

    /**
     * 关联款式ID（用于产销关联）
     */
    private Long styleId;
}
