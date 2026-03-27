package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDetailDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderQuery;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDetailQuery;
import com.duoduo.jxc.entity.PurchaseOrder;

public interface PurchaseOrderService extends IService<PurchaseOrder> {

    PageResult<PurchaseOrderDTO> pageQuery(PurchaseOrderQuery query);
    PageResult<PurchaseOrderDetailDTO> detailPageQuery(PurchaseOrderDetailQuery query);
    PurchaseOrderDTO getDetail(Long orderId);

    Long createOrder(PurchaseOrderDTO dto);

    void updateOrder(PurchaseOrderDTO dto);

    void deleteOrder(Long orderId);

    void auditOrder(Long orderId);
}
