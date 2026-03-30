package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("jxc_fabric_inventory")
public class FabricInventory {
    @TableId(type = IdType.AUTO)
    private Long inventoryId;
    private Long fabricId;
    private Long warehouseId;
    private BigDecimal quantity;
    @TableField("available_qty")
    private BigDecimal availableQty;
    @TableField("locked_qty")
    private BigDecimal lockedQty;
    private String batchNo;
    private BigDecimal costPrice;
    private LocalDateTime updateTime;
}
