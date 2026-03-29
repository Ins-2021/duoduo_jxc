package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_style_colorway")
public class StyleColorway extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long colorwayId;
    private Long styleId;
    private String colorwayNo;
    private String colorwayName;
    private String mainColor;
    private String accentColor;
    private String colorImages;
    private Integer isActive;
}
