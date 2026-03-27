package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_print_template")
public class PrintTemplate extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long templateId;

    private String templateName;

    private String bizType;

    private String paperType;

    private Integer paperWidthMm;

    private Integer paperHeightMm;

    private String designJson;

    private Integer isDefault;

    private Integer enabled;
}

