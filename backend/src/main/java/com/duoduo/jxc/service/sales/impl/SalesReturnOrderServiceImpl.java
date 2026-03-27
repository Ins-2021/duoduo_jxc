package com.duoduo.jxc.service.sales.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.sales.SalesReturnOrderConverter;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnDetailDTO;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnOrderDTO;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnOrderQuery;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnSourceDetailDTO;
import com.duoduo.jxc.dto.sales.return_order.SalesReturnSourceOrderDTO;
import com.duoduo.jxc.entity.Customer;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.SalesOrderDetail;
import com.duoduo.jxc.entity.sales.SalesReturnDetail;
import com.duoduo.jxc.entity.sales.SalesReturnOrder;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.CustomerMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.mapper.SalesOrderDetailMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.mapper.sales.SalesReturnDetailMapper;
import com.duoduo.jxc.mapper.sales.SalesReturnOrderMapper;
import com.duoduo.jxc.service.sales.SalesReturnOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesReturnOrderServiceImpl extends ServiceImpl<SalesReturnOrderMapper, SalesReturnOrder> implements SalesReturnOrderService {

    private final SalesReturnDetailMapper detailMapper;
    private final SalesReturnOrderConverter converter;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderDetailMapper salesOrderDetailMapper;
    private final CustomerMapper customerMapper;
    private final ProductSpuMapper productSpuMapper;
    private final ProductSkuMapper productSkuMapper;

    @Override
    public PageResult<SalesReturnOrderDTO> pageQuery(SalesReturnOrderQuery query) {
        Page<SalesReturnOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SalesReturnOrder> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(query.getStatus() != null, SalesReturnOrder::getStatus, query.getStatus())
               .eq(query.getCustomerId() != null, SalesReturnOrder::getCustomerId, query.getCustomerId())
               .like(StringUtils.hasText(query.getDocNo()), SalesReturnOrder::getDocNo, query.getDocNo())
               .orderByDesc(SalesReturnOrder::getCreateTime);

        page(page, wrapper);
        List<SalesReturnOrderDTO> dtoList = converter.toDTOList(page.getRecords());
        dtoList.forEach(this::fillOrderExtra);
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public SalesReturnOrderDTO getDetail(Long orderId) {
        SalesReturnOrder order = getById(orderId);
        if (order == null) {
            return null;
        }

        SalesReturnOrderDTO dto = converter.toDTO(order);
        LambdaQueryWrapper<SalesReturnDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(SalesReturnDetail::getOrderId, orderId);
        List<SalesReturnDetail> details = detailMapper.selectList(detailWrapper);
        List<SalesReturnDetailDTO> detailDtos = converter.toDetailDTOList(details);
        fillOrderExtra(dto);
        fillDetailExtra(detailDtos, buildSalesDetailMap(order.getOriginSalesId()), buildApprovedReturnedQtyMap(order.getOriginSalesId(), null));
        dto.setDetails(detailDtos);
        return dto;
    }

    @Override
    public SalesReturnSourceOrderDTO getSourceOrder(Long orderId) {
        SalesOrder salesOrder = getSourceSalesOrder(orderId);
        List<SalesOrderDetail> sourceDetails = loadSalesDetails(orderId);
        Map<Long, Integer> returnedQtyMap = buildApprovedReturnedQtyMap(orderId, null);
        Map<Long, ProductSpu> spuMap = loadSpuMap(sourceDetails);
        Map<Long, ProductSku> skuMap = loadSkuMap(sourceDetails);

        SalesReturnSourceOrderDTO dto = new SalesReturnSourceOrderDTO();
        dto.setOrderId(salesOrder.getOrderId());
        dto.setDocNo(salesOrder.getDocNo());
        dto.setDocDate(salesOrder.getDocDate());
        dto.setCustomerId(salesOrder.getCustomerId());
        dto.setCustomerName(resolveCustomerName(salesOrder.getCustomerId()));
        dto.setActualAmount(salesOrder.getActualAmount());
        dto.setDetails(sourceDetails.stream().map(detail -> {
            SalesReturnSourceDetailDTO item = new SalesReturnSourceDetailDTO();
            item.setDetailId(detail.getDetailId());
            item.setSkuId(detail.getSkuId());
            item.setSpuId(detail.getSpuId());
            item.setQty(detail.getQty());
            item.setReturnedQty(returnedQtyMap.getOrDefault(detail.getDetailId(), 0));
            item.setAvailableQty(Math.max(0, (detail.getQty() == null ? 0 : detail.getQty()) - item.getReturnedQty()));
            item.setUnitPrice(detail.getUnitPrice());
            item.setLineAmount(detail.getLineAmount());
            item.setWarehouseId(detail.getWarehouseId());
            item.setRemark(detail.getRemark());
            item.setProductName(resolveProductName(detail.getSpuId(), spuMap));
            item.setProductInfo(buildProductInfo(detail.getSpuId(), detail.getSkuId(), spuMap, skuMap));
            return item;
        }).toList());
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(SalesReturnOrderDTO dto) {
        ValidationResult validationResult = validateOrder(dto, null);
        SalesReturnOrder order = converter.toEntity(dto);
        order.setDocNo("TH" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + System.currentTimeMillis());
        if (order.getStatus() == null) {
            order.setStatus(10);
        }
        if (order.getDocDate() == null) {
            order.setDocDate(LocalDate.now());
        }
        order.setCustomerId(validationResult.customerId);
        order.setRefundAmount(validationResult.refundAmount);
        save(order);

        persistDetails(order.getOrderId(), validationResult.details);
        return order.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(SalesReturnOrderDTO dto) {
        SalesReturnOrder existed = getById(dto.getOrderId());
        if (existed == null) {
            throw new BusinessException(BizCode.SALES_RETURN_ORDER_NOT_FOUND);
        }
        if (Objects.equals(existed.getStatus(), 20)) {
            throw new BusinessException(BizCode.SALES_RETURN_CANNOT_MODIFY);
        }

        if (dto.getOriginSalesId() == null) {
            dto.setOriginSalesId(existed.getOriginSalesId());
        }
        ValidationResult validationResult = validateOrder(dto, dto.getOrderId());
        SalesReturnOrder order = converter.toEntity(dto);
        order.setCustomerId(validationResult.customerId);
        order.setRefundAmount(validationResult.refundAmount);
        updateById(order);

        deleteDetailsByOrderId(order.getOrderId());
        persistDetails(order.getOrderId(), validationResult.details);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId) {
        SalesReturnOrder existed = getById(orderId);
        if (existed == null) {
            return;
        }
        if (Objects.equals(existed.getStatus(), 20)) {
            throw new BusinessException(BizCode.SALES_RETURN_CANNOT_DELETE);
        }
        removeById(orderId);
        deleteDetailsByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long orderId) {
        SalesReturnOrder existed = getById(orderId);
        if (existed == null) {
            throw new BusinessException(BizCode.SALES_RETURN_ORDER_NOT_FOUND);
        }
        if (Objects.equals(existed.getStatus(), 20)) {
            return;
        }

        SalesReturnOrderDTO dto = getDetail(orderId);
        validateOrder(dto, orderId);

        SalesReturnOrder order = new SalesReturnOrder();
        order.setOrderId(orderId);
        order.setStatus(20);
        updateById(order);
    }

    private void persistDetails(Long orderId, List<SalesReturnDetail> details) {
        details.forEach(d -> {
            d.setDetailId(null);
            d.setOrderId(orderId);
            detailMapper.insert(d);
        });
    }

    private void deleteDetailsByOrderId(Long orderId) {
        LambdaQueryWrapper<SalesReturnDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SalesReturnDetail::getOrderId, orderId);
        detailMapper.delete(deleteWrapper);
    }

    private ValidationResult validateOrder(SalesReturnOrderDTO dto, Long currentOrderId) {
        if (dto == null || dto.getOriginSalesId() == null) {
            throw new BusinessException(BizCode.SALES_RETURN_NO_SOURCE);
        }
        if (dto.getDetails() == null || dto.getDetails().isEmpty()) {
            throw new BusinessException(BizCode.SALES_RETURN_NO_ITEMS);
        }

        SalesOrder salesOrder = getSourceSalesOrder(dto.getOriginSalesId());
        if (dto.getCustomerId() != null && !Objects.equals(dto.getCustomerId(), salesOrder.getCustomerId())) {
            throw new BusinessException(BizCode.SALES_RETURN_CUSTOMER_MISMATCH);
        }

        Map<Long, SalesOrderDetail> salesDetailMap = buildSalesDetailMap(dto.getOriginSalesId());
        Map<Long, Integer> returnedQtyMap = buildApprovedReturnedQtyMap(dto.getOriginSalesId(), currentOrderId);
        Set<Long> detailIds = dto.getDetails().stream()
            .map(SalesReturnDetailDTO::getOriginSalesDetailId)
            .collect(Collectors.toSet());
        if (detailIds.contains(null) || detailIds.size() != dto.getDetails().size()) {
            throw new BusinessException(BizCode.SALES_RETURN_ITEM_DUPLICATE);
        }

        List<SalesReturnDetail> detailEntities = new ArrayList<>();
        BigDecimal refundAmount = BigDecimal.ZERO;
        for (SalesReturnDetailDTO detailDTO : dto.getDetails()) {
            SalesOrderDetail sourceDetail = salesDetailMap.get(detailDTO.getOriginSalesDetailId());
            if (sourceDetail == null) {
                throw new BusinessException(BizCode.BAD_REQUEST, "存在无效的原销售明细");
            }
            Integer qty = detailDTO.getQty();
            if (qty == null || qty <= 0) {
                throw new BusinessException(BizCode.BAD_REQUEST, "退货数量必须大于0");
            }
            int sourceQty = sourceDetail.getQty() == null ? 0 : sourceDetail.getQty();
            int returnedQty = returnedQtyMap.getOrDefault(sourceDetail.getDetailId(), 0);
            int availableQty = Math.max(0, sourceQty - returnedQty);
            if (qty > availableQty) {
                throw new BusinessException(BizCode.SALES_RETURN_QTY_EXCEEDED);
            }

            SalesReturnDetail detail = converter.toDetailEntity(detailDTO);
            detail.setOriginSalesDetailId(sourceDetail.getDetailId());
            detail.setOriginQty(sourceQty);
            detail.setSpuId(sourceDetail.getSpuId());
            detail.setSkuId(sourceDetail.getSkuId());
            detail.setWarehouseId(detail.getWarehouseId() == null ? sourceDetail.getWarehouseId() : detail.getWarehouseId());
            BigDecimal unitPrice = detail.getUnitPrice() == null ? sourceDetail.getUnitPrice() : detail.getUnitPrice();
            detail.setUnitPrice(unitPrice);
            BigDecimal lineAmount = calculateLineAmount(unitPrice, qty);
            detail.setLineAmount(lineAmount);
            refundAmount = refundAmount.add(lineAmount);
            detailEntities.add(detail);
        }
        return new ValidationResult(detailEntities, refundAmount, salesOrder.getCustomerId());
    }

    private BigDecimal calculateLineAmount(BigDecimal unitPrice, Integer qty) {
        return (unitPrice == null ? BigDecimal.ZERO : unitPrice).multiply(BigDecimal.valueOf(qty == null ? 0L : qty.longValue()));
    }

    private SalesOrder getSourceSalesOrder(Long originSalesId) {
        SalesOrder salesOrder = salesOrderMapper.selectById(originSalesId);
        if (salesOrder == null) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
        }
        if (Objects.equals(salesOrder.getOrderType(), 3)) {
            throw new BusinessException(BizCode.SALES_ORDER_CANNOT_RETURN);
        }
        return salesOrder;
    }

    private List<SalesOrderDetail> loadSalesDetails(Long orderId) {
        if (orderId == null) {
            return List.of();
        }
        LambdaQueryWrapper<SalesOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesOrderDetail::getOrderId, orderId);
        return salesOrderDetailMapper.selectList(wrapper);
    }

    private Map<Long, SalesOrderDetail> buildSalesDetailMap(Long originSalesId) {
        return loadSalesDetails(originSalesId).stream()
            .collect(Collectors.toMap(SalesOrderDetail::getDetailId, item -> item, (a, b) -> a, LinkedHashMap::new));
    }

    private Map<Long, Integer> buildApprovedReturnedQtyMap(Long originSalesId, Long excludeOrderId) {
        if (originSalesId == null) {
            return Map.of();
        }
        LambdaQueryWrapper<SalesReturnOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(SalesReturnOrder::getOriginSalesId, originSalesId)
            .eq(SalesReturnOrder::getStatus, 20)
            .ne(excludeOrderId != null, SalesReturnOrder::getOrderId, excludeOrderId);
        List<SalesReturnOrder> approvedOrders = list(orderWrapper);
        if (approvedOrders.isEmpty()) {
            return Map.of();
        }
        List<Long> orderIds = approvedOrders.stream().map(SalesReturnOrder::getOrderId).toList();
        LambdaQueryWrapper<SalesReturnDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(SalesReturnDetail::getOrderId, orderIds);
        List<SalesReturnDetail> details = detailMapper.selectList(detailWrapper);
        Map<Long, Integer> returnedQtyMap = new LinkedHashMap<>();
        for (SalesReturnDetail detail : details) {
            if (detail.getOriginSalesDetailId() == null || detail.getQty() == null) {
                continue;
            }
            returnedQtyMap.merge(detail.getOriginSalesDetailId(), detail.getQty(), Integer::sum);
        }
        return returnedQtyMap;
    }

    private void fillOrderExtra(SalesReturnOrderDTO dto) {
        if (dto == null || dto.getCustomerId() == null) {
            return;
        }
        dto.setCustomerName(resolveCustomerName(dto.getCustomerId()));
    }

    private String resolveCustomerName(Long customerId) {
        if (customerId == null) {
            return "";
        }
        Customer customer = customerMapper.selectById(customerId);
        return customer == null ? "" : customer.getCustomerName();
    }

    private void fillDetailExtra(List<SalesReturnDetailDTO> details, Map<Long, SalesOrderDetail> salesDetailMap, Map<Long, Integer> returnedQtyMap) {
        if (details == null || details.isEmpty()) {
            return;
        }
        Map<Long, ProductSpu> spuMap = loadSpuMapFromReturnDetails(details);
        Map<Long, ProductSku> skuMap = loadSkuMapFromReturnDetails(details);
        for (SalesReturnDetailDTO detail : details) {
            SalesOrderDetail sourceDetail = salesDetailMap.get(detail.getOriginSalesDetailId());
            if (sourceDetail == null) {
                continue;
            }
            int originQty = sourceDetail.getQty() == null ? 0 : sourceDetail.getQty();
            int returnedQty = returnedQtyMap.getOrDefault(sourceDetail.getDetailId(), 0);
            detail.setOriginQty(originQty);
            detail.setReturnedQty(returnedQty);
            detail.setAvailableQty(Math.max(0, originQty - returnedQty));
            detail.setProductName(resolveProductName(detail.getSpuId(), spuMap));
            detail.setProductInfo(buildProductInfo(detail.getSpuId(), detail.getSkuId(), spuMap, skuMap));
        }
    }

    private Map<Long, ProductSpu> loadSpuMap(List<SalesOrderDetail> details) {
        List<Long> spuIds = details.stream().map(SalesOrderDetail::getSpuId).filter(Objects::nonNull).distinct().toList();
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        LambdaQueryWrapper<ProductSpu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductSpu::getSpuId, spuIds);
        return productSpuMapper.selectList(wrapper).stream()
            .collect(Collectors.toMap(ProductSpu::getSpuId, item -> item, (a, b) -> a, LinkedHashMap::new));
    }

    private Map<Long, ProductSku> loadSkuMap(List<SalesOrderDetail> details) {
        List<Long> skuIds = details.stream().map(SalesOrderDetail::getSkuId).filter(Objects::nonNull).distinct().toList();
        if (skuIds.isEmpty()) {
            return Map.of();
        }
        LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductSku::getSkuId, skuIds);
        return productSkuMapper.selectList(wrapper).stream()
            .collect(Collectors.toMap(ProductSku::getSkuId, item -> item, (a, b) -> a, LinkedHashMap::new));
    }

    private Map<Long, ProductSpu> loadSpuMapFromReturnDetails(List<SalesReturnDetailDTO> details) {
        List<Long> spuIds = details.stream().map(SalesReturnDetailDTO::getSpuId).filter(Objects::nonNull).distinct().toList();
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        LambdaQueryWrapper<ProductSpu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductSpu::getSpuId, spuIds);
        return productSpuMapper.selectList(wrapper).stream()
            .collect(Collectors.toMap(ProductSpu::getSpuId, item -> item, (a, b) -> a, LinkedHashMap::new));
    }

    private Map<Long, ProductSku> loadSkuMapFromReturnDetails(List<SalesReturnDetailDTO> details) {
        List<Long> skuIds = details.stream().map(SalesReturnDetailDTO::getSkuId).filter(Objects::nonNull).distinct().toList();
        if (skuIds.isEmpty()) {
            return Map.of();
        }
        LambdaQueryWrapper<ProductSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductSku::getSkuId, skuIds);
        return productSkuMapper.selectList(wrapper).stream()
            .collect(Collectors.toMap(ProductSku::getSkuId, item -> item, (a, b) -> a, LinkedHashMap::new));
    }

    private String resolveProductName(Long spuId, Map<Long, ProductSpu> spuMap) {
        ProductSpu spu = spuMap.get(spuId);
        return spu == null ? "" : spu.getSpuName();
    }

    private String buildProductInfo(Long spuId, Long skuId, Map<Long, ProductSpu> spuMap, Map<Long, ProductSku> skuMap) {
        String productName = resolveProductName(spuId, spuMap);
        ProductSku sku = skuMap.get(skuId);
        String attr1 = sku != null && StringUtils.hasText(sku.getAttr1()) ? sku.getAttr1().trim() : "";
        String attr2 = sku != null && StringUtils.hasText(sku.getAttr2()) ? sku.getAttr2().trim() : "";
        String attrs = List.of(attr1, attr2).stream().filter(StringUtils::hasText).collect(Collectors.joining(" / "));
        if (!StringUtils.hasText(productName)) {
            return attrs;
        }
        return StringUtils.hasText(attrs) ? productName + " " + attrs : productName;
    }

    private static class ValidationResult {
        private final List<SalesReturnDetail> details;
        private final BigDecimal refundAmount;
        private final Long customerId;

        private ValidationResult(List<SalesReturnDetail> details, BigDecimal refundAmount, Long customerId) {
            this.details = details;
            this.refundAmount = refundAmount;
            this.customerId = customerId;
        }
    }
}
