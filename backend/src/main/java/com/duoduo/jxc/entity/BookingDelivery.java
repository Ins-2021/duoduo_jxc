package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预订单发货记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jxc_booking_delivery")
public class BookingDelivery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long deliveryId;

    /**
     * 预订单ID
     */
    private Long bookingOrderId;

    /**
     * 预订单明细ID
     */
    private Long bookingDetailId;

    /**
     * 本次转化数量
     */
    private Integer convertQty;

    /**
     * 关联的销售单ID（转单后填写）
     */
    private Long salesOrderId;

    /**
     * 出库仓库ID
     */
    private Long warehouseId;

    /**
     * 操作人ID
     */
    private Long operatorId;
}
