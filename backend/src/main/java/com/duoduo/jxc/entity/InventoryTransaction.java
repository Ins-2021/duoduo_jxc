package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存流水实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_inventory_transaction")
public class InventoryTransaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long transactionId;

    /**
     * 仓库ID
     */
    private Long warehouseId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 流水类型：1-入库 2-出库 3-锁定 4-解锁 5-盘点调整
     */
    private Integer transType;

    /**
     * 变动数量（正数）
     */
    private Integer qty;

    /**
     * 变动前可用库存
     */
    private Integer beforeQty;

    /**
     * 变动后可用库存
     */
    private Integer afterQty;

    /**
     * 源单据类型
     */
    private String billType;

    /**
     * 源单据ID
     */
    private Long billId;

    /**
     * 源单据编号
     */
    private String billNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人ID
     */
    private Long operatorId;
}
