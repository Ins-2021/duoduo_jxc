package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SKU计件溢价配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_sku_price_premium")
public class SkuPricePremium extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** SKU ID */
    private Long skuId;
    /** 工序ID */
    private Long processId;
    /** 溢价类型(size/fabric/complexity) */
    private String premiumType;
    /** 溢价值(如: "XL","2XL" 或面料难度等级) */
    private String premiumValue;
    /** 溢价比例(百分比, 如 120 表示1.2倍) */
    private BigDecimal premiumRate;
    /** 是否启用 */
    private Integer enabled;
    /** 备注 */
    private String remark;
}
