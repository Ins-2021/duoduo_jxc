package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("jxc_size_chart")
public class SizeChart extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long chartId;
    private Long standardId;
    private Long categoryId;
    private String sizeCode;
    private java.math.BigDecimal bust;
    private java.math.BigDecimal waist;
    private java.math.BigDecimal hip;
    private java.math.BigDecimal shoulderWidth;
    private java.math.BigDecimal sleeveLength;
    private java.math.BigDecimal length;
    private java.math.BigDecimal inseam;
}
