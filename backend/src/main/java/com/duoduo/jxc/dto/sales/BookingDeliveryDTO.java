package com.duoduo.jxc.dto.sales;

import lombok.Data;
import java.util.List;

/**
 * 预订单发货DTO
 */
@Data
public class BookingDeliveryDTO {

    /**
     * 预订单ID
     */
    private Long bookingOrderId;

    /**
     * 发货明细列表
     */
    private List<BookingDeliveryItemDTO> items;
}
