package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_fabric")
public class Fabric {
    @TableId(type = IdType.AUTO)
    private Long fabricId;
    private String fabricCode;
    private String fabricName;
    private String fabricType;
    private String composition;
    private String width;
    private String weight;
    private String color;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Long supplierId;
    private BigDecimal minStock;
    private BigDecimal maxStock;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
