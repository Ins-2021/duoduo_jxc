package com.duoduo.jxc.inventory;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.entity.StockInOut;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.StockInOutMapper;
import com.duoduo.jxc.service.StockInOutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class StockInOutServiceTest {

    @Autowired
    private StockInOutService stockInOutService;

    @Autowired
    private StockInOutMapper stockInOutMapper;

    private StockInOut createPendingOrder() {
        StockInOut order = new StockInOut();
        order.setBillNo("QT" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        order.setType(1);
        order.setWarehouseId(1L);
        order.setWarehouseName("测试仓库");
        order.setStatus(0);
        stockInOutMapper.insert(order);
        return order;
    }

    @Test
    void pageList_shouldReturnPagedResult() {
        var page = stockInOutService.pageList(new com.duoduo.jxc.common.PageQuery());
        assertNotNull(page);
        assertNotNull(page.getList());
        assertTrue(page.getTotal() >= 0);
    }

    @Test
    void getById_shouldReturnNullForNonExistent() {
        var result = stockInOutService.getById(999999L);
        assertNull(result);
    }

    @Test
    void approve_shouldThrowWhenOrderNotFound() {
        assertThrows(BusinessException.class, () -> stockInOutService.approve(999999L));
    }

    @Test
    void approve_shouldThrowWhenNotPendingStatus() {
        StockInOut order = createPendingOrder();
        order.setStatus(1);
        stockInOutMapper.updateById(order);

        assertThrows(BusinessException.class, () -> stockInOutService.approve(order.getInOutId()));
    }

    @Test
    void approve_shouldSucceedForPendingOrder() {
        StockInOut order = createPendingOrder();
        assertDoesNotThrow(() -> stockInOutService.approve(order.getInOutId()));

        StockInOut updated = stockInOutMapper.selectById(order.getInOutId());
        assertEquals(1, updated.getStatus());
    }

    @Test
    void delete_shouldRemoveOrder() {
        StockInOut order = createPendingOrder();
        assertDoesNotThrow(() -> stockInOutService.delete(order.getInOutId()));
        assertNull(stockInOutMapper.selectById(order.getInOutId()));
    }
}
