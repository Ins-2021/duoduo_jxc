package com.duoduo.jxc.purchase;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderQuery;
import com.duoduo.jxc.entity.PurchaseOrder;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PurchaseOrderMapper;
import com.duoduo.jxc.service.PurchaseOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class PurchaseOrderServiceTest {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    private PurchaseOrder createDraftOrder() {
        PurchaseOrder order = new PurchaseOrder();
        order.setDocNo("CG" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        order.setSupplierId(1L);
        order.setStatus(10);
        order.setTotalAmount(BigDecimal.ZERO);
        purchaseOrderMapper.insert(order);
        return order;
    }

    @Test
    void pageQuery_shouldReturnPagedResult() {
        PurchaseOrderQuery query = new PurchaseOrderQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        var page = purchaseOrderService.pageQuery(query);
        assertNotNull(page);
        assertNotNull(page.getList());
        assertTrue(page.getTotal() >= 0);
    }

    @Test
    void getDetail_shouldReturnNullForNonExistent() {
        var result = purchaseOrderService.getDetail(999999L);
        assertNull(result);
    }

    @Test
    void auditOrder_shouldThrowWhenOrderNotFound() {
        assertThrows(BusinessException.class, () -> purchaseOrderService.auditOrder(999999L));
    }

    @Test
    void auditOrder_shouldThrowWhenNotDraftStatus() {
        PurchaseOrder order = createDraftOrder();
        order.setStatus(20);
        purchaseOrderMapper.updateById(order);

        assertThrows(BusinessException.class, () -> purchaseOrderService.auditOrder(order.getOrderId()));
    }

    @Test
    void auditOrder_shouldSucceedForDraftOrder() {
        PurchaseOrder order = createDraftOrder();
        assertDoesNotThrow(() -> purchaseOrderService.auditOrder(order.getOrderId()));

        PurchaseOrder updated = purchaseOrderMapper.selectById(order.getOrderId());
        assertEquals(20, updated.getStatus());
    }

    @Test
    void deleteOrder_shouldRemoveOrder() {
        PurchaseOrder order = createDraftOrder();
        assertDoesNotThrow(() -> purchaseOrderService.deleteOrder(order.getOrderId()));
        assertNull(purchaseOrderMapper.selectById(order.getOrderId()));
    }
}
