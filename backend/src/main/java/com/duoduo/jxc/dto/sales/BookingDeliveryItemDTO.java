package com.duoduo.jxc.dto.sales;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 预订单发货明细项DTO
 */
@Data
public class BookingDeliveryItemDTO {

    /**
     * 预订单明细ID
     */
    private Long bookingDetailId;

    /**
     * 本次转化数量
     */
    private Integer convertQty;

    /**
     * 出库仓库ID
     */
    private Long warehouseId;

    /**
     * 预设销售单价
     */
    private BigDecimal unitPrice;
}
