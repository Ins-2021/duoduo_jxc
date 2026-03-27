package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_inventory_check_detail")
public class InventoryCheckDetail {
    @TableId(type = IdType.AUTO)
    private Long detailId;
    private Long checkId;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String attr1;
    private String attr2;
    private Integer systemQty;
    private Integer actualQty;
    private Integer diffQty;
    private BigDecimal costPrice;
    private BigDecimal diffAmount;
    @TableLogic
    private Integer deleted;
}
