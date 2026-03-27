package com.duoduo.jxc.service.sales;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnOrderDTO;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnOrderQuery;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnSourceOrderDTO;

public interface SalesReturnOrderService {
    PageResult<SalesReturnOrderDTO> pageQuery(SalesReturnOrderQuery query);
    SalesReturnOrderDTO getDetail(Long orderId);
    SalesReturnSourceOrderDTO getSourceOrder(Long orderId);
    Long createOrder(SalesReturnOrderDTO dto);
    void updateOrder(SalesReturnOrderDTO dto);
    void deleteOrder(Long orderId);
    void auditOrder(Long orderId);
}
