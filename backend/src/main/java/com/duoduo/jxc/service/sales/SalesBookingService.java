package com.duoduo.jxc.service.sales;

import com.duoduo.jxc.dto.sales.BookingDeliveryDTO;
import com.duoduo.jxc.dto.sales.BookingUnfulfilledDTO;

import java.util.List;

/**
 * 销售预订单Service接口
 */
public interface SalesBookingService {

    /**
     * 预订单分批发货（支持部分发货）
     *
     * @param bookingOrderId 预订单ID
     * @param dto 发货明细
     * @return 新建的销售单ID
     */
    Long partialDelivery(Long bookingOrderId, BookingDeliveryDTO dto);

    /**
     * 查询预订单欠货明细
     *
     * @param bookingOrderId 预订单ID
     * @return 欠货明细列表
     */
    List<BookingUnfulfilledDTO> getUnfulfilled(Long bookingOrderId);
}
