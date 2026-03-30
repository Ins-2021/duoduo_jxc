package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.dto.print.PrintDataDTO;
import com.duoduo.jxc.dto.print.PrintDataItemDTO;
import com.duoduo.jxc.entity.PrintTemplate;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.SalesOrderDetail;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PrintTemplateMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.mapper.SalesOrderDetailMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.PrintDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintDataServiceImpl implements PrintDataService {

    private final PrintTemplateMapper printTemplateMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderDetailMapper salesOrderDetailMapper;
    private final ProductSpuMapper productSpuMapper;
    private final ProductSkuMapper productSkuMapper;

    @Override
    public PrintDataDTO previewData(Long templateId, String bizId) {
        if (templateId == null) {
            throw new BusinessException(BizCode.PRINT_TEMPLATE_ID_EMPTY);
        }
        PrintTemplate tpl = printTemplateMapper.selectById(templateId);
        if (tpl == null) {
            throw new BusinessException(BizCode.PRINT_TEMPLATE_NOT_FOUND);
        }
        String bizType = tpl.getBizType();

        if (StringUtils.hasText(bizId) && bizType != null && bizType.contains("销售")) {
            try {
                long orderId = Long.parseLong(bizId);
                PrintDataDTO fromSales = buildFromSalesOrder(orderId);
                if (fromSales != null) {
                    return fromSales;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return sample(bizType);
    }

    private PrintDataDTO buildFromSalesOrder(long orderId) {
        SalesOrder order = salesOrderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        List<SalesOrderDetail> details = salesOrderDetailMapper.selectList(new LambdaQueryWrapper<SalesOrderDetail>()
                .eq(SalesOrderDetail::getOrderId, orderId));
        Map<Long, String> spuNameMap = loadSpuNameMap(details);
        Map<Long, ProductSku> skuMap = loadSkuMap(details);

        PrintDataDTO dto = new PrintDataDTO();
        dto.setBillNo(order.getDocNo());
        dto.setBillDate(order.getDocDate() == null ? null : order.getDocDate().toString());
        dto.setStoreName("");
        dto.setCustomerName("");
        dto.setOperator("");
        dto.setRemark(order.getRemark());
        dto.setTotalAmount(order.getActualAmount() == null ? order.getTotalAmount() : order.getActualAmount());

        List<PrintDataItemDTO> items = details.stream().map(d -> {
            PrintDataItemDTO i = new PrintDataItemDTO();
            i.setName(spuNameMap.getOrDefault(d.getSpuId(), ""));
            i.setQty(d.getQty());
            i.setPrice(d.getUnitPrice());
            i.setAmount(d.getLineAmount());
            ProductSku sku = d.getSkuId() != null ? skuMap.get(d.getSkuId()) : null;
            if (sku != null) {
                i.setAttr1(sku.getAttr1());
                i.setAttr2(sku.getAttr2());
            }
            return i;
        }).toList();
        dto.setItems(items);
        return dto;
    }

    private Map<Long, ProductSku> loadSkuMap(List<SalesOrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return Map.of();
        }
        Set<Long> skuIds = details.stream().map(SalesOrderDetail::getSkuId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (skuIds.isEmpty()) {
            return Map.of();
        }
        List<ProductSku> skus = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>()
                .in(ProductSku::getSkuId, skuIds)
                .eq(ProductSku::getDeleted, 0));
        return skus.stream().collect(Collectors.toMap(ProductSku::getSkuId, s -> s, (a, b) -> a));
    }

    private Map<Long, String> loadSpuNameMap(List<SalesOrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return Map.of();
        }
        Set<Long> spuIds = details.stream().map(SalesOrderDetail::getSpuId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        List<ProductSpu> spus = productSpuMapper.selectList(new LambdaQueryWrapper<ProductSpu>()
                .in(ProductSpu::getSpuId, spuIds)
                .eq(ProductSpu::getDeleted, 0));
        return spus.stream().collect(Collectors.toMap(ProductSpu::getSpuId, s -> s.getSpuName() == null ? "" : s.getSpuName(), (a, b) -> a));
    }

    private PrintDataDTO sample(String bizType) {
        PrintDataDTO dto = new PrintDataDTO();
        dto.setBillNo((bizType == null ? "单据" : bizType) + "TEST" + System.currentTimeMillis());
        dto.setBillDate(LocalDate.now().toString());
        dto.setStoreName("衣多多");
        dto.setCustomerName("默认客户");
        dto.setOperator("管理员");
        dto.setRemark("预览示例数据");
        dto.setTotalAmount(new BigDecimal("1200"));
        PrintDataItemDTO item = new PrintDataItemDTO();
        item.setName("商品A");
        item.setQty(2);
        item.setPrice(new BigDecimal("100"));
        item.setAmount(new BigDecimal("200"));
        dto.setItems(List.of(item));
        return dto;
    }
}

