package com.duoduo.jxc.sales;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.service.SalesOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
class SalesOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalesOrderService salesOrderService;

    @Test
    @WithMockUser(authorities = "sales:order:add")
    void createOrder_validData_shouldReturn200() throws Exception {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setOrderType(1);
        dto.setDocDate(LocalDate.now());
        dto.setTotalAmount(new BigDecimal("100.00"));
        dto.setOperatorId(1L);

        when(salesOrderService.createOrder(any(SalesOrderDTO.class))).thenReturn(1L);

        mockMvc.perform(post("/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1L));
    }

    @Test
    @WithMockUser(authorities = "sales:order:audit")
    void auditOrder_validId_shouldReturn200() throws Exception {
        mockMvc.perform(post("/sales/1/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(salesOrderService).auditOrder(1L);
    }

    @Test
    @WithMockUser(authorities = "sales:order:audit")
    void auditOrder_alreadyAudited_shouldReturnError() throws Exception {
        doThrow(new BusinessException("单据已审核，无法再次审核")).when(salesOrderService).auditOrder(2L);

        mockMvc.perform(post("/sales/2/audit"))
                .andExpect(status().isOk()) // GlobalExceptionHandler 拦截返回 500 code
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("单据已审核，无法再次审核"));
    }

    @Test
    @WithMockUser(authorities = "sales:order:audit")
    void unauditOrder_validId_shouldReturn200() throws Exception {
        mockMvc.perform(post("/sales/1/unaudit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(salesOrderService).unauditOrder(1L);
    }

    @Test
    @WithMockUser(authorities = "sales:order:audit")
    void unauditOrder_financialLocked_shouldReturnError() throws Exception {
        doThrow(new BusinessException("该单据已产生关联财务核销，禁止反审核")).when(salesOrderService).unauditOrder(3L);

        mockMvc.perform(post("/sales/3/unaudit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("该单据已产生关联财务核销，禁止反审核"));
    }

    @Test
    @WithMockUser(authorities = "sales:order:convert")
    void convertToSales_validId_shouldReturn200() throws Exception {
        mockMvc.perform(post("/sales/1/convert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(salesOrderService).convertToSales(1L);
    }

    @Test
    @WithMockUser(authorities = "sales:order:delete")
    void deleteOrder_validId_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(salesOrderService).deleteOrder(1L);
    }

    @Test
    @WithMockUser(authorities = "sales:order:delete")
    void deleteOrder_auditedOrder_shouldReturnError() throws Exception {
        doThrow(new BusinessException("已审核单据不允许删除")).when(salesOrderService).deleteOrder(4L);

        mockMvc.perform(delete("/sales/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("已审核单据不允许删除"));
    }

    @Test
    @WithMockUser(authorities = "sales:order:view")
    void getById_validId_shouldReturnData() throws Exception {
        SalesOrderDTO dto = new SalesOrderDTO();
        dto.setOrderId(1L);
        dto.setDocNo("XS20260328001");
        
        when(salesOrderService.getDetail(1L)).thenReturn(dto);

        mockMvc.perform(get("/sales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.docNo").value("XS20260328001"));
    }

    @Test
    @WithMockUser
    // 带有默认用户但无特定权限注解，测试无权限拦截
    void getById_noPermission_shouldReturn403() throws Exception {
        mockMvc.perform(get("/sales/1"))
                .andExpect(status().isForbidden());
    }
}