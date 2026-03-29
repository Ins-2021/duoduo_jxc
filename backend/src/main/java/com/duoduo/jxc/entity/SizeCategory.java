package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_size_category")
public class SizeCategory extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long categoryId;
    private String name;
    private String code;
    private String description;
    private Integer sortOrder;
    private Integer isActive;
}
