package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_product_attribute")
public class ProductAttribute extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    
    private Integer sort;

    private Integer status;
}
