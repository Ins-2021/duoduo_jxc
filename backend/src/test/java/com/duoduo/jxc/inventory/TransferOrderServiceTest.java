package com.duoduo.jxc.inventory;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.entity.TransferOrder;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.TransferOrderMapper;
import com.duoduo.jxc.service.TransferOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class TransferOrderServiceTest {

    @Autowired
    private TransferOrderService transferOrderService;

    @Autowired
    private TransferOrderMapper transferOrderMapper;

    private TransferOrder createPendingOrder() {
        TransferOrder order = new TransferOrder();
        order.setTransferNo("DB" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        order.setFromWarehouseId(1L);
        order.setFromWarehouseName("仓库A");
        order.setToWarehouseId(2L);
        order.setToWarehouseName("仓库B");
        order.setStatus(0);
        transferOrderMapper.insert(order);
        return order;
    }

    @Test
    void approve_pendingOrder_shouldSucceed() {
        TransferOrder order = createPendingOrder();
        transferOrderService.approve(order.getTransferId());

        TransferOrder updated = transferOrderMapper.selectById(order.getTransferId());
        assertEquals(1, updated.getStatus());
    }

    @Test
    void approve_notFound_shouldThrow() {
        assertThrows(BusinessException.class, () -> transferOrderService.approve(999999L));
    }

    @Test
    void approve_alreadyApproved_shouldThrow() {
        TransferOrder order = createPendingOrder();
        transferOrderService.approve(order.getTransferId());

        assertThrows(BusinessException.class, () -> transferOrderService.approve(order.getTransferId()));
    }

    @Test
    void reject_pendingOrder_shouldSucceed() {
        TransferOrder order = createPendingOrder();
        transferOrderService.reject(order.getTransferId());

        TransferOrder updated = transferOrderMapper.selectById(order.getTransferId());
        assertEquals(4, updated.getStatus());
    }

    @Test
    void reject_notFound_shouldThrow() {
        assertThrows(BusinessException.class, () -> transferOrderService.reject(999999L));
    }

    @Test
    void reject_alreadyApproved_shouldThrow() {
        TransferOrder order = createPendingOrder();
        transferOrderService.approve(order.getTransferId());

        assertThrows(BusinessException.class, () -> transferOrderService.reject(order.getTransferId()));
    }

    @Test
    void getById_existing_shouldReturnDto() {
        TransferOrder order = createPendingOrder();
        var dto = transferOrderService.getById(order.getTransferId());
        assertNotNull(dto);
        assertEquals(order.getTransferNo(), dto.getTransferNo());
    }

    @Test
    void getById_notFound_shouldReturnNull() {
        assertNull(transferOrderService.getById(999999L));
    }

    @Test
    void delete_shouldRemoveOrder() {
        TransferOrder order = createPendingOrder();
        transferOrderService.delete(order.getTransferId());
        assertNull(transferOrderMapper.selectById(order.getTransferId()));
    }
}
