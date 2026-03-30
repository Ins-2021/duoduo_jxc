package com.duoduo.jxc.print;

import com.duoduo.jxc.JxcApplication;
import com.duoduo.jxc.dto.print.PrintDataDTO;
import com.duoduo.jxc.entity.PrintTemplate;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.SalesOrderDetail;
import com.duoduo.jxc.mapper.PrintTemplateMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.mapper.SalesOrderDetailMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.PrintDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JxcApplication.class, properties = "spring.profiles.active=test")
public class PrintDataServiceIT {

    @Autowired
    private PrintTemplateMapper printTemplateMapper;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private SalesOrderDetailMapper salesOrderDetailMapper;

    @Autowired
    private ProductSpuMapper productSpuMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Autowired
    private PrintDataService printDataService;

    @Test
    void previewData_sales_shouldUseOrderDataWhenBizIdProvided() {
        ProductSpu spu = new ProductSpu();
        spu.setSpuName("商品A");
        spu.setStatus(1);
        productSpuMapper.insert(spu);

        SalesOrder order = new SalesOrder();
        order.setDocNo("XS202603240001");
        order.setOrderType(1);
        order.setDocDate(LocalDate.now());
        order.setTotalAmount(new BigDecimal("200"));
        order.setActualAmount(new BigDecimal("200"));
        order.setStatus(10);
        order.setSettleStatus(0);
        order.setDeleted(0);
        salesOrderMapper.insert(order);

        ProductSku sku = new ProductSku();
        sku.setSpuId(spu.getSpuId());
        sku.setSkuCode("SKU001");
        sku.setAttr1("黑色");
        sku.setAttr2("XL");
        sku.setStatus(1);
        sku.setDeleted(0);
        productSkuMapper.insert(sku);

        SalesOrderDetail d = new SalesOrderDetail();
        d.setOrderId(order.getOrderId());
        d.setSkuId(sku.getSkuId());
        d.setSpuId(spu.getSpuId());
        d.setQty(2);
        d.setUnitPrice(new BigDecimal("100"));
        d.setLineAmount(new BigDecimal("200"));
        d.setDeleted(0);
        salesOrderDetailMapper.insert(d);

        PrintTemplate tpl = new PrintTemplate();
        tpl.setTemplateName("销售单");
        tpl.setBizType("销售");
        tpl.setEnabled(1);
        tpl.setIsDefault(1);
        printTemplateMapper.insert(tpl);

        PrintDataDTO data = printDataService.previewData(tpl.getTemplateId(), String.valueOf(order.getOrderId()));
        assertEquals("XS202603240001", data.getBillNo());
        assertNotNull(data.getItems());
        assertFalse(data.getItems().isEmpty());
        assertEquals("商品A", data.getItems().get(0).getName());
        assertEquals(2, data.getItems().get(0).getQty());
        assertEquals("黑色", data.getItems().get(0).getAttr1());
        assertEquals("XL", data.getItems().get(0).getAttr2());
    }
}
