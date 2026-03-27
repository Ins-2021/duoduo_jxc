package com.duoduo.jxc.inventory;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.entity.AssemblyOrder;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.AssemblyOrderMapper;
import com.duoduo.jxc.service.AssemblyOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class AssemblyOrderServiceTest {

    @Autowired
    private AssemblyOrderService assemblyOrderService;

    @Autowired
    private AssemblyOrderMapper assemblyOrderMapper;

    private AssemblyOrder createPendingOrder() {
        AssemblyOrder order = new AssemblyOrder();
        order.setAssemblyNo("ZZ" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        order.setWarehouseId(1L);
        order.setWarehouseName("仓库A");
        order.setType(1);
        order.setStatus(0);
        assemblyOrderMapper.insert(order);
        return order;
    }

    @Test
    void approve_pendingOrder_shouldSucceed() {
        AssemblyOrder order = createPendingOrder();
        assemblyOrderService.approve(order.getAssemblyId());

        AssemblyOrder updated = assemblyOrderMapper.selectById(order.getAssemblyId());
        assertEquals(1, updated.getStatus());
    }

    @Test
    void approve_notFound_shouldThrow() {
        assertThrows(BusinessException.class, () -> assemblyOrderService.approve(999999L));
    }

    @Test
    void approve_alreadyApproved_shouldThrow() {
        AssemblyOrder order = createPendingOrder();
        assemblyOrderService.approve(order.getAssemblyId());

        assertThrows(BusinessException.class, () -> assemblyOrderService.approve(order.getAssemblyId()));
    }

    @Test
    void delete_shouldRemoveOrder() {
        AssemblyOrder order = createPendingOrder();
        assemblyOrderService.delete(order.getAssemblyId());
        assertNull(assemblyOrderMapper.selectById(order.getAssemblyId()));
    }

    @Test
    void getById_existing_shouldReturnDto() {
        AssemblyOrder order = createPendingOrder();
        var dto = assemblyOrderService.getById(order.getAssemblyId());
        assertNotNull(dto);
        assertEquals(order.getAssemblyNo(), dto.getAssemblyNo());
    }

    @Test
    void getById_notFound_shouldReturnNull() {
        assertNull(assemblyOrderService.getById(999999L));
    }
}
