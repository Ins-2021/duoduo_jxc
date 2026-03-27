package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_assembly_order_detail")
public class AssemblyOrderDetail {
    @TableId(type = IdType.AUTO)
    private Long detailId;
    private Long assemblyId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String attr1;
    private String attr2;
    private Integer qty;
    private BigDecimal costPrice;
    private Integer itemType;
    @TableLogic
    private Integer deleted;
}
