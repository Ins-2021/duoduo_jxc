package com.duoduo.jxc.service.impl.sales;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.dto.sales.*;
import com.duoduo.jxc.entity.BookingDelivery;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.SalesOrderDetail;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.BookingDeliveryMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.SalesOrderDetailMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.SalesOrderService;
import com.duoduo.jxc.service.sales.SalesBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 销售预订单Service实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SalesBookingServiceImpl implements SalesBookingService {

    private final SalesOrderService salesOrderService;
    private final SalesOrderDetailMapper detailMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final BookingDeliveryMapper bookingDeliveryMapper;
    private final InventoryService inventoryService;
    private final ProductSkuMapper productSkuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long partialDelivery(Long bookingOrderId, BookingDeliveryDTO dto) {
        // 1. 参数校验
        if (CollectionUtils.isEmpty(dto.getItems())) {
            throw new BusinessException(BizCode.BOOKING_DELIVERY_EMPTY);
        }

        // 校验每个发货项
        for (BookingDeliveryItemDTO item : dto.getItems()) {
            if (item.getConvertQty() == null || item.getConvertQty() <= 0) {
                throw new BusinessException(BizCode.BOOKING_DELIVERY_QTY_INVALID);
            }
        }

        // 2. 查询预订单（加悲观锁），校验状态
        LambdaQueryWrapper<SalesOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(SalesOrder::getOrderId, bookingOrderId)
                    .last("FOR UPDATE");
        SalesOrder bookingOrder = salesOrderMapper.selectOne(orderWrapper);
        if (bookingOrder == null) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
        }
        if (bookingOrder.getOrderType() != 3) {
            throw new BusinessException(BizCode.BOOKING_ORDER_INVALID);
        }
        if (bookingOrder.getStatus() != 30) {
            throw new BusinessException(BizCode.BOOKING_ORDER_STATUS_ERROR);
        }

        // 3. 查询预订单明细
        LambdaQueryWrapper<SalesOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(SalesOrderDetail::getOrderId, bookingOrderId);
        List<SalesOrderDetail> bookingDetails = detailMapper.selectList(detailWrapper);

        // 4. 处理发货明细
        List<SalesOrderDetailDTO> salesDetailDTOs = new ArrayList<>();

        for (BookingDeliveryItemDTO item : dto.getItems()) {
            // 查找对应的预订单明细
            SalesOrderDetail bookingDetail = bookingDetails.stream()
                    .filter(d -> Objects.equals(d.getDetailId(), item.getBookingDetailId()))
                    .findFirst()
                    .orElse(null);

            if (bookingDetail == null) {
                throw new BusinessException(BizCode.BOOKING_DETAIL_NOT_FOUND);
            }

            // 校验未发货数量
            if (item.getConvertQty() > bookingDetail.getUnfulfilledQty()) {
                throw new BusinessException(BizCode.BOOKING_DELIVERY_QTY_EXCEED);
            }

            Long warehouseId = item.getWarehouseId() != null ? item.getWarehouseId() : bookingDetail.getWarehouseId();

            // 扣减库存（从锁定库存中扣减，而非可用库存）
            inventoryService.deductFromLocked(
                    warehouseId,
                    bookingDetail.getSkuId(),
                    item.getConvertQty(),
                    "SALES_ORDER",
                    bookingOrderId,
                    bookingOrder.getDocNo()
            );

            // 更新预订单明细的未发货数量和已发货数量
            int newDeliveryQty = (bookingDetail.getDeliveryQty() != null ? bookingDetail.getDeliveryQty() : 0) + item.getConvertQty();
            int newUnfulfilledQty = bookingDetail.getUnfulfilledQty() - item.getConvertQty();

            SalesOrderDetail updateDetail = new SalesOrderDetail();
            updateDetail.setDetailId(bookingDetail.getDetailId());
            updateDetail.setDeliveryQty(newDeliveryQty);
            updateDetail.setUnfulfilledQty(newUnfulfilledQty);
            detailMapper.updateById(updateDetail);

            // 记录发货记录
            BookingDelivery delivery = new BookingDelivery();
            delivery.setBookingOrderId(bookingOrderId);
            delivery.setBookingDetailId(item.getBookingDetailId());
            delivery.setConvertQty(item.getConvertQty());
            delivery.setWarehouseId(warehouseId);
            bookingDeliveryMapper.insert(delivery);

            // 准备销售单明细
            SalesOrderDetailDTO salesDetail = new SalesOrderDetailDTO();
            salesDetail.setSkuId(bookingDetail.getSkuId());
            salesDetail.setSpuId(bookingDetail.getSpuId());
            salesDetail.setQty(item.getConvertQty());
            salesDetail.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice() : bookingDetail.getUnitPrice());
            salesDetail.setWarehouseId(warehouseId);
            salesDetailDTOs.add(salesDetail);
        }

        // 5. 创建新销售单（批发单）
        SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
        salesOrderDTO.setOrderType(1); // 批发单
        salesOrderDTO.setCustomerId(bookingOrder.getCustomerId());
        salesOrderDTO.setStoreId(bookingOrder.getStoreId());
        salesOrderDTO.setDetails(salesDetailDTOs);

        Long newSalesOrderId = salesOrderService.createOrder(salesOrderDTO);

        // 更新发货记录的关联销售单ID
        LambdaQueryWrapper<BookingDelivery> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(BookingDelivery::getBookingOrderId, bookingOrderId)
                .isNull(BookingDelivery::getSalesOrderId);
        BookingDelivery updateDelivery = new BookingDelivery();
        updateDelivery.setSalesOrderId(newSalesOrderId);
        bookingDeliveryMapper.update(updateDelivery, updateWrapper);

        // 6. 判断是否全部发货完成
        boolean allDelivered = true;
        LambdaQueryWrapper<SalesOrderDetail> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(SalesOrderDetail::getOrderId, bookingOrderId);
        List<SalesOrderDetail> allDetails = detailMapper.selectList(checkWrapper);

        for (SalesOrderDetail detail : allDetails) {
            if (detail.getUnfulfilledQty() != null && detail.getUnfulfilledQty() > 0) {
                allDelivered = false;
                break;
            }
        }

        if (allDelivered) {
            // 全部发货完成，更新预订单状态为已完成
            SalesOrder updateOrder = new SalesOrder();
            updateOrder.setOrderId(bookingOrderId);
            updateOrder.setStatus(40); // 已完成
            salesOrderMapper.updateById(updateOrder);

            // 解锁剩余锁定库存（如果有未发货部分被锁定但订单完成）
            for (SalesOrderDetail detail : allDetails) {
                if (detail.getWarehouseId() != null && detail.getSkuId() != null
                        && detail.getUnfulfilledQty() != null && detail.getUnfulfilledQty() > 0) {
                    inventoryService.unlockStock(
                            detail.getWarehouseId(),
                            detail.getSkuId(),
                            detail.getUnfulfilledQty(),
                            bookingOrderId
                    );
                    log.info("预订单完成，解锁剩余库存: orderId={}, skuId={}, qty={}",
                            bookingOrderId, detail.getSkuId(), detail.getUnfulfilledQty());
                }
            }

            log.info("预订单全部发货完成，状态更新为已完成: bookingOrderId={}", bookingOrderId);
        }

        return newSalesOrderId;
    }

    @Override
    public List<BookingUnfulfilledDTO> getUnfulfilled(Long bookingOrderId) {
        // 查询预订单明细
        LambdaQueryWrapper<SalesOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalesOrderDetail::getOrderId, bookingOrderId);
        List<SalesOrderDetail> details = detailMapper.selectList(wrapper);

        // 收集所有 SKU ID
        List<Long> skuIds = details.stream()
                .map(SalesOrderDetail::getSkuId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询 SKU 信息
        Map<Long, ProductSku> skuMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(skuIds)) {
            List<ProductSku> skus = productSkuMapper.selectBatchIds(skuIds);
            skuMap = skus.stream()
                    .collect(Collectors.toMap(ProductSku::getSkuId, Function.identity()));
        }

        List<BookingUnfulfilledDTO> result = new ArrayList<>();

        for (SalesOrderDetail detail : details) {
            // 只返回未发货数量大于0的明细
            if (detail.getUnfulfilledQty() != null && detail.getUnfulfilledQty() > 0) {
                BookingUnfulfilledDTO dto = new BookingUnfulfilledDTO();
                dto.setDetailId(detail.getDetailId());
                dto.setSkuId(detail.getSkuId());

            // 从 SKU 信息中获取编码和属性
            ProductSku sku = skuMap.get(detail.getSkuId());
            if (sku != null) {
                dto.setSkuCode(sku.getSkuCode() != null ? sku.getSkuCode() : "");
                // SKU名称由属性组合而成
                String skuName = "";
                if (sku.getAttr1() != null) {
                    skuName = sku.getAttr1();
                }
                if (sku.getAttr2() != null) {
                    skuName = skuName.isEmpty() ? sku.getAttr2() : skuName + " " + sku.getAttr2();
                }
                dto.setSkuName(skuName);
            } else {
                dto.setSkuCode("");
                dto.setSkuName("");
            }

                dto.setBookedQty(detail.getBookedQty());
                dto.setDeliveryQty(detail.getDeliveryQty() != null ? detail.getDeliveryQty() : 0);
                dto.setUnfulfilledQty(detail.getUnfulfilledQty());
                dto.setUnitPrice(detail.getUnitPrice());
                result.add(dto);
            }
        }

        return result;
    }
}
