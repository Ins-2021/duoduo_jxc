package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_product_category")
public class ProductCategory extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long categoryId;

    private Long parentId;

    private String categoryName;

    private Integer sort;

    private Integer status;
}
