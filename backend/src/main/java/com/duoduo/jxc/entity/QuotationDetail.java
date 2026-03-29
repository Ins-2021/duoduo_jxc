package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_quotation_detail")
public class QuotationDetail extends BaseEntity {
    @TableId(type = IdType.AUTO)
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
