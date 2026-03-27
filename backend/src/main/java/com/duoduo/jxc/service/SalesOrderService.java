package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.dto.sales.SalesOrderDetailDTO;
import com.duoduo.jxc.dto.sales.SalesOrderQuery;
import com.duoduo.jxc.dto.sales.SalesOrderDetailQuery;
import com.duoduo.jxc.entity.SalesOrder;

public interface SalesOrderService extends IService<SalesOrder> {

    PageResult<SalesOrderDTO> pageQuery(SalesOrderQuery query);
    PageResult<SalesOrderDetailDTO> detailPageQuery(SalesOrderDetailQuery query);
    SalesOrderDTO getDetail(Long orderId);

    Long createOrder(SalesOrderDTO dto);

    void updateOrder(SalesOrderDTO dto);

    void deleteOrder(Long orderId);

    void auditOrder(Long orderId);

    void unauditOrder(Long orderId);

    void convertToSales(Long bookingOrderId);
}
