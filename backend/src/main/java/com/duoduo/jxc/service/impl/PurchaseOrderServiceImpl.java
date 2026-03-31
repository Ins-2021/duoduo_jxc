package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.PurchaseOrderConverter;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDetailDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderQuery;
import com.duoduo.jxc.enums.PurchaseOrderStatusEnum;
import com.duoduo.jxc.entity.PurchaseOrder;
import com.duoduo.jxc.entity.PurchaseOrderDetail;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.entity.StockInOut;
import com.duoduo.jxc.entity.StockInOutDetail;
import com.duoduo.jxc.enums.StockInOutStatusEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PurchaseOrderDetailMapper;
import com.duoduo.jxc.mapper.PurchaseOrderMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.StockInOutDetailMapper;
import com.duoduo.jxc.mapper.StockInOutMapper;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    private final PurchaseOrderDetailMapper detailMapper;
    private final PurchaseOrderConverter converter;
    private final StockInOutMapper stockInOutMapper;
    private final StockInOutDetailMapper stockInOutDetailMapper;
    private final InventoryService inventoryService;
    private final ProductSkuMapper productSkuMapper;

    @Override
    public PageResult<PurchaseOrderDTO> pageQuery(PurchaseOrderQuery query) {
        Page<PurchaseOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(query.getOrderType() != null, PurchaseOrder::getOrderType, query.getOrderType())
               .eq(query.getStatus() != null, PurchaseOrder::getStatus, query.getStatus())
               .eq(query.getSupplierId() != null, PurchaseOrder::getSupplierId, query.getSupplierId())
               .like(StringUtils.hasText(query.getDocNo()), PurchaseOrder::getDocNo, query.getDocNo())
               .orderByDesc(PurchaseOrder::getCreateTime);

        page(page, wrapper);
        List<PurchaseOrderDTO> dtoList = converter.toDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PageResult<PurchaseOrderDetailDTO> detailPageQuery(com.duoduo.jxc.dto.purchase.PurchaseOrderDetailQuery query) {
        Page<PurchaseOrderDetail> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PurchaseOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getSkuId() != null, PurchaseOrderDetail::getSkuId, query.getSkuId())
               .eq(query.getSpuId() != null, PurchaseOrderDetail::getSpuId, query.getSpuId())
               .orderByDesc(PurchaseOrderDetail::getDetailId);
        
        if (query.getDocNo() != null || query.getSupplierId() != null || query.getOrderType() != null) {
            LambdaQueryWrapper<PurchaseOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(query.getSupplierId() != null, PurchaseOrder::getSupplierId, query.getSupplierId())
                       .eq(query.getOrderType() != null, PurchaseOrder::getOrderType, query.getOrderType())
                       .like(StringUtils.hasText(query.getDocNo()), PurchaseOrder::getDocNo, query.getDocNo());
            List<PurchaseOrder> orders = list(orderWrapper);
            if (orders.isEmpty()) {
                return new PageResult<>(0L, List.of());
            }
            List<Long> orderIds = orders.stream().map(PurchaseOrder::getOrderId).toList();
            wrapper.in(PurchaseOrderDetail::getOrderId, orderIds);
        }

        detailMapper.selectPage(page, wrapper);
        List<PurchaseOrderDetailDTO> dtoList = converter.toDetailDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PurchaseOrderDTO getDetail(Long orderId) {
        PurchaseOrder order = getById(orderId);
        if (order == null) return null;
        
        PurchaseOrderDTO dto = converter.toDTO(order);
        LambdaQueryWrapper<PurchaseOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(PurchaseOrderDetail::getOrderId, orderId);
        List<PurchaseOrderDetail> details = detailMapper.selectList(detailWrapper);
        dto.setDetails(converter.toDetailDTOList(details));
        
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(PurchaseOrderDTO dto) {
        BigDecimal totalAmount = prepareOrderDetails(dto.getDetails());
        PurchaseOrder order = converter.toEntity(dto);
        // 生成单号
        String prefix = dto.getOrderType() == 1 ? "JH" : "JY";
        order.setDocNo(prefix + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + System.currentTimeMillis());
        if (order.getStatus() == null) {
            order.setStatus(PurchaseOrderStatusEnum.DRAFT.getCode());
        }
        if (order.getDocDate() == null) {
            order.setDocDate(LocalDate.now());
        }
        
        // 新增金额兜底处理
        if (order.getDiscountAmount() == null) order.setDiscountAmount(BigDecimal.ZERO);
        if (order.getOtherFee() == null) order.setOtherFee(BigDecimal.ZERO);
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount.subtract(order.getDiscountAmount()).add(order.getOtherFee()));
        
        save(order);

        List<PurchaseOrderDetailDTO> details = dto.getDetails();
        if (details != null && !details.isEmpty()) {
            List<PurchaseOrderDetail> detailEntities = converter.toDetailEntityList(details);
            detailEntities.forEach(d -> {
                d.setOrderId(order.getOrderId());
                detailMapper.insert(d);
            });
        }
        return order.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(PurchaseOrderDTO dto) {
        assertDraftOrder(dto.getOrderId(), BizCode.PURCHASE_ORDER_CANNOT_MODIFY);
        BigDecimal totalAmount = prepareOrderDetails(dto.getDetails());
        PurchaseOrder order = converter.toEntity(dto);
        
        if (order.getDiscountAmount() == null) order.setDiscountAmount(BigDecimal.ZERO);
        if (order.getOtherFee() == null) order.setOtherFee(BigDecimal.ZERO);
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount.subtract(order.getDiscountAmount()).add(order.getOtherFee()));
        
        updateById(order);

        LambdaQueryWrapper<PurchaseOrderDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PurchaseOrderDetail::getOrderId, order.getOrderId());
        detailMapper.delete(deleteWrapper);

        List<PurchaseOrderDetailDTO> details = dto.getDetails();
        if (details != null && !details.isEmpty()) {
            List<PurchaseOrderDetail> detailEntities = converter.toDetailEntityList(details);
            detailEntities.forEach(d -> {
                d.setOrderId(order.getOrderId());
                d.setDetailId(null);
                detailMapper.insert(d);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId) {
        assertDraftOrder(orderId, BizCode.PURCHASE_ORDER_CANNOT_DELETE);
        removeById(orderId);
        LambdaQueryWrapper<PurchaseOrderDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PurchaseOrderDetail::getOrderId, orderId);
        detailMapper.delete(deleteWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long orderId) {
        PurchaseOrder exist = getById(orderId);
        if (exist == null) {
            throw new BusinessException(BizCode.PURCHASE_ORDER_NOT_FOUND);
        }
        if (PurchaseOrderStatusEnum.DRAFT.getCode() != exist.getStatus()) {
            throw new BusinessException(BizCode.ORDER_NOT_DRAFT);
        }

        // 进货单(type=1): 自动生成入库单并增加库存
        // 进货退货单(type=3): 自动生成出库单并扣减库存
        if (exist.getOrderType() != null && (exist.getOrderType() == 1 || exist.getOrderType() == 3)) {
            generateStockInOutAndAdjustInventory(exist);
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderId(orderId);
        order.setStatus(PurchaseOrderStatusEnum.RUNNING.getCode());
        updateById(order);
    }

    private void assertDraftOrder(Long orderId, BizCode bizCode) {
        PurchaseOrder exist = getById(orderId);
        if (exist == null) {
            throw new BusinessException(BizCode.PURCHASE_ORDER_NOT_FOUND);
        }
        if (PurchaseOrderStatusEnum.DRAFT.getCode() != exist.getStatus()) {
            throw new BusinessException(bizCode);
        }
    }

    private void generateStockInOutAndAdjustInventory(PurchaseOrder purchaseOrder) {
        List<PurchaseOrderDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderDetail>()
                        .eq(PurchaseOrderDetail::getOrderId, purchaseOrder.getOrderId()));
        if (details == null || details.isEmpty()) {
            return;
        }

        boolean isReturn = purchaseOrder.getOrderType() == 3;
        int stockType = isReturn ? 2 : 1; // 1-入库 2-出库
        Long warehouseId = details.stream()
                .map(PurchaseOrderDetail::getWarehouseId)
                .filter(w -> w != null)
                .findFirst()
                .orElse(null);

        // 生成出入库单（直接审核通过，状态=1）
        StockInOut stockInOut = new StockInOut();
        stockInOut.setBillNo("RK" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase());
        stockInOut.setType(stockType);
        stockInOut.setWarehouseId(warehouseId);
        stockInOut.setStatus(StockInOutStatusEnum.APPROVED.getValue()); // 直接已审核
        stockInOut.setBillDate(LocalDateTime.now());
        stockInOut.setRemark("采购单[" + purchaseOrder.getDocNo() + "]自动生成");
        stockInOut.setCreateTime(LocalDateTime.now());
        stockInOut.setUpdateTime(LocalDateTime.now());
        stockInOutMapper.insert(stockInOut);

        // 复制明细并调整库存
        for (PurchaseOrderDetail detail : details) {
            if (detail.getSkuId() == null || detail.getQty() == null || detail.getQty() <= 0) {
                continue;
            }

            StockInOutDetail sioDetail = new StockInOutDetail();
            sioDetail.setInOutId(stockInOut.getInOutId());
            sioDetail.setSkuId(detail.getSkuId());
            sioDetail.setQty(detail.getQty());
            sioDetail.setCostPrice(detail.getUnitPrice());
            stockInOutDetailMapper.insert(sioDetail);

            if (detail.getWarehouseId() != null) {
                String billType = isReturn ? "PURCHASE_RETURN" : "PURCHASE_IN";
                if (isReturn) {
                    inventoryService.deductStock(detail.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                            billType, purchaseOrder.getOrderId(), purchaseOrder.getDocNo());
                } else {
                    inventoryService.addStock(detail.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                            billType, purchaseOrder.getOrderId(), purchaseOrder.getDocNo());
                }
            }
        }

        log.info("采购单[{}]审核自动生成出入库单[{}], type={}, 明细数={}",
                purchaseOrder.getDocNo(), stockInOut.getBillNo(), stockType, details.size());
    }

    private BigDecimal prepareOrderDetails(List<PurchaseOrderDetailDTO> details) {
        if (details == null || details.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Set<Long> skuIds = details.stream()
                .map(PurchaseOrderDetailDTO::getSkuId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, ProductSku> skuMap = skuIds.isEmpty() ? Map.of() : productSkuMapper.selectBatchIds(skuIds).stream()
                .collect(Collectors.toMap(ProductSku::getSkuId, sku -> sku));
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderDetailDTO detail : details) {
            if (detail.getSkuId() == null) {
                throw new BusinessException(BizCode.BAD_REQUEST, "采购订单明细缺少skuId");
            }
            ProductSku sku = skuMap.get(detail.getSkuId());
            if (sku == null) {
                throw new BusinessException(BizCode.NOT_FOUND, "SKU不存在: " + detail.getSkuId());
            }
            detail.setSpuId(sku.getSpuId());
            BigDecimal lineAmount = BigDecimal.ZERO;
            if (detail.getQty() != null && detail.getUnitPrice() != null) {
                lineAmount = BigDecimal.valueOf(detail.getQty()).multiply(detail.getUnitPrice());
            }
            detail.setLineAmount(lineAmount);
            totalAmount = totalAmount.add(lineAmount);
        }
        return totalAmount;
    }
}
