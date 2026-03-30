package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * BOM尺码映射规则
 * 解决同款不同码，辅料用量或型号不同的问题
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bom_size_mapping")
public class BomSizeMapping extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bomId;
    
    private Long bomItemId;

    /**
     * 产品尺码
     */
    private String productSize;

    /**
     * 实际使用的物料ID
     */
    private Long actualMaterialId;

    /**
     * 实际调整后的用量
     */
    private BigDecimal actualUsage;
}
