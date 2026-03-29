package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_inventory")
public class Inventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 库存ID
     */
    @TableId(type = IdType.AUTO)
    private Long inventoryId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 总库存数量
     */
    private Integer qty;

    /**
     * 可用库存（总库存-已锁定）
     */
    @TableField("available_qty")
    private Integer availableQty;

    /**
     * 已锁定库存
     */
    @TableField("locked_qty")
    private Integer lockedQty;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;
}
