package com.duoduo.jxc.controller.sales;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.BookingDeliveryDTO;
import com.duoduo.jxc.dto.sales.BookingUnfulfilledDTO;
import com.duoduo.jxc.service.sales.SalesBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 销售预订单控制器
 */
@RestController
@RequestMapping("/sales/booking")
@RequiredArgsConstructor
public class SalesBookingController {

    private final SalesBookingService salesBookingService;

    /**
     * 预订单分批发货
     * POST /api/sales/booking/{bookingOrderId}/delivery
     */
    @PostMapping("/{bookingOrderId}/delivery")
    @PreAuthorize("@perm.has('sales:booking:delivery')")
    public Result<Long> partialDelivery(
            @PathVariable Long bookingOrderId,
            @RequestBody BookingDeliveryDTO dto) {
        return Result.success(salesBookingService.partialDelivery(bookingOrderId, dto));
    }

    /**
     * 查询预订单欠货明细
     * GET /api/sales/booking/{bookingOrderId}/unfulfilled
     */
    @GetMapping("/{bookingOrderId}/unfulfilled")
    @PreAuthorize("@perm.has('sales:booking:view')")
    public Result<List<BookingUnfulfilledDTO>> getUnfulfilled(@PathVariable Long bookingOrderId) {
        return Result.success(salesBookingService.getUnfulfilled(bookingOrderId));
    }
}
