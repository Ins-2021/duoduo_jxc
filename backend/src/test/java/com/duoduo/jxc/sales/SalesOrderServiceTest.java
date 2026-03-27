package com.duoduo.jxc.sales;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.SalesOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class SalesOrderServiceTest {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    private SalesOrder createOrder(int status, String docNoPrefix) {
        SalesOrder order = new SalesOrder();
        order.setDocNo(docNoPrefix + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        order.setOrderType(1);
        order.setStatus(status);
        order.setDocDate(LocalDate.now());
        order.setOperatorId(1L);
        order.setDeleted(0);
        salesOrderMapper.insert(order);
        return order;
    }

    @Test
    void createOrder_defaultStatus_shouldBeDraft() {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setOrderType(1);
        dto.setDocDate(LocalDate.now());
        dto.setTotalAmount(new BigDecimal("100"));
        dto.setOperatorId(1L);

        Long orderId = salesOrderService.createOrder(dto);
        assertNotNull(orderId);

        SalesOrder saved = salesOrderMapper.selectById(orderId);
        assertNotNull(saved);
        assertEquals(10, saved.getStatus());
        assertNotNull(saved.getDocNo());
        assertTrue(saved.getDocNo().startsWith("XS"));
    }

    @Test
    void createOrder_preOrder_shouldUseYDPrefix() {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setOrderType(3);
        dto.setDocDate(LocalDate.now());
        dto.setTotalAmount(new BigDecimal("200"));
        dto.setOperatorId(1L);

        Long orderId = salesOrderService.createOrder(dto);
        SalesOrder saved = salesOrderMapper.selectById(orderId);
        assertTrue(saved.getDocNo().startsWith("YD"));
    }

    @Test
    void auditOrder_draft_shouldApprove() {
        SalesOrder order = createOrder(10, "XS_AUDIT_");

        salesOrderService.auditOrder(order.getOrderId());
        SalesOrder updated = salesOrderMapper.selectById(order.getOrderId());
        assertEquals(30, updated.getStatus());
    }

    @Test
    void auditOrder_notFound_shouldThrow() {
        assertThrows(BusinessException.class, () -> salesOrderService.auditOrder(999999L));
    }

    @Test
    void auditOrder_alreadyAudited_shouldThrow() {
        SalesOrder order = createOrder(30, "XS_DOUBLE_");

        assertThrows(BusinessException.class, () -> salesOrderService.auditOrder(order.getOrderId()));
    }

    @Test
    void getDetail_existing_shouldReturnDto() {
        SalesOrder order = createOrder(10, "XS_DETAIL_");
        order.setTotalAmount(new BigDecimal("50"));
        salesOrderMapper.updateById(order);

        SalesOrderDTO dto = salesOrderService.getDetail(order.getOrderId());
        assertNotNull(dto);
        assertNotNull(dto.getDocNo());
    }

    @Test
    void getDetail_notFound_shouldReturnNull() {
        assertNull(salesOrderService.getDetail(999999L));
    }

    @Test
    void deleteOrder_shouldRemove() {
        SalesOrder order = createOrder(10, "XS_DEL_");

        salesOrderService.deleteOrder(order.getOrderId());
        assertNull(salesOrderMapper.selectById(order.getOrderId()));
    }
}
