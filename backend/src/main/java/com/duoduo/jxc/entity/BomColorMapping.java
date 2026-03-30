package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BOM颜色映射规则
 * 解决同款不同色，用料（面辅料）不同的问题
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bom_color_mapping")
public class BomColorMapping extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bomId;
    
    private Long bomItemId;

    /**
     * 产品颜色
     */
    private String productColor;

    /**
     * 实际使用的物料ID
     */
    private Long actualMaterialId;

    /**
     * 实际使用的物料颜色
     */
    private String actualMaterialColor;
}
