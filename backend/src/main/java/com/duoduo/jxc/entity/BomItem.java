package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_bom_item")
public class BomItem extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long itemId;
    private Long bomId;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String color;
    private String size;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal wastageRate;
    private Integer sortOrder;
}
