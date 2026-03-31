package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_size_category")
public class SizeCategory extends BaseEntity {
    @TableId(value = "size_category_id", type = IdType.AUTO)
    private Long categoryId;
    private String name;
    private String code;
    @TableField(exist = false)
    private String description;
    private Integer sortOrder;
    private Integer isActive;
}
