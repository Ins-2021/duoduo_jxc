package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("jxc_inventory_alert")
public class InventoryAlert {
    @TableId(type = IdType.AUTO)
    private Long alertId;
    private Long warehouseId;
    private String warehouseName;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String attr1;
    private String attr2;
    private Integer currentQty;
    private Integer minQty;
    private Integer maxQty;
    private Integer alertType;
    private Integer status;
    private LocalDateTime alertTime;
}
