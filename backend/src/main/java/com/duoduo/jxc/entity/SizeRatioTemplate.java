package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_size_ratio_template")
public class SizeRatioTemplate extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long templateId;
    private String name;
    private Long categoryId;
    private String ratios;
    private String description;
}
