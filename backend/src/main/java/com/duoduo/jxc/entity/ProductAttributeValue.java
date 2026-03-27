package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_product_attribute_value")
public class ProductAttributeValue extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long attributeId;

    private String value;

    private Integer sort;
}
