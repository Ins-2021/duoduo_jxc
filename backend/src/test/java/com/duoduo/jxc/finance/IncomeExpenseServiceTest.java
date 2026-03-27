package com.duoduo.jxc.finance;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.entity.IncomeExpense;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.IncomeExpenseMapper;
import com.duoduo.jxc.service.IncomeExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
class IncomeExpenseServiceTest {

    @Autowired
    private IncomeExpenseService incomeExpenseService;

    @Autowired
    private IncomeExpenseMapper incomeExpenseMapper;

    private IncomeExpense createDraftOrder() {
        IncomeExpense order = new IncomeExpense();
        order.setBillNo("SR" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        order.setType(1);
        order.setAmount(new BigDecimal("100.00"));
        order.setBillDate(LocalDateTime.now());
        order.setStatus(0);
        incomeExpenseMapper.insert(order);
        return order;
    }

    @Test
    void pageList_shouldReturnPagedResult() {
        var page = incomeExpenseService.pageList(new PageQuery());
        assertNotNull(page);
        assertNotNull(page.getList());
        assertTrue(page.getTotal() >= 0);
    }

    @Test
    void getById_shouldReturnNullForNonExistent() {
        var result = incomeExpenseService.getById(999999L);
        assertNull(result);
    }

    @Test
    void complete_shouldThrowWhenOrderNotFound() {
        assertThrows(BusinessException.class, () -> incomeExpenseService.approve(999999L));
    }

    @Test
    void complete_shouldUpdateStatus() {
        IncomeExpense order = createDraftOrder();
        assertDoesNotThrow(() -> incomeExpenseService.approve(order.getIeId()));

        IncomeExpense updated = incomeExpenseMapper.selectById(order.getIeId());
        assertEquals(1, updated.getStatus());
    }

    @Test
    void delete_shouldRemoveOrder() {
        IncomeExpense order = createDraftOrder();
        assertDoesNotThrow(() -> incomeExpenseService.delete(order.getIeId()));
        assertNull(incomeExpenseMapper.selectById(order.getIeId()));
    }
}
