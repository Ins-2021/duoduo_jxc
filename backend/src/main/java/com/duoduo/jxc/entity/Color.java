package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_color")
public class Color extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long colorId;
    private String name;
    private String code;
    private String colorValue;
    private String pantoneNo;
    private Integer sortOrder;
    private Integer isActive;
}
