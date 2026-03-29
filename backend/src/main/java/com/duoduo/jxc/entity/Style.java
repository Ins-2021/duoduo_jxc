package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_style")
public class Style extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long styleId;
    private String styleNo;
    private String styleName;
    private Integer year;
    private String season;
    private String series;
    private Long categoryId;
    private Long brandId;
    private Long designerId;
    private String styleType;
    private String targetGender;
    private String targetAgeGroup;
    private String designImage;
    private String sampleImage;
    private String techPack;
    private Integer status;
}
