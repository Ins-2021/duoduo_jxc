package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("jxc_inventory")
public class Inventory {
    @TableId(type = IdType.AUTO)
    private Long inventoryId;
    private Long warehouseId;
    private Long skuId;
    private Integer qty;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
