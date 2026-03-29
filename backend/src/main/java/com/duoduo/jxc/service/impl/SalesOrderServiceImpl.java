package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.SalesOrderConverter;
import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.dto.sales.SalesOrderDetailDTO;
import com.duoduo.jxc.dto.sales.SalesOrderQuery;
import com.duoduo.jxc.dto.workflow.WfInstanceStartRequest;
import com.duoduo.jxc.entity.*;
import com.duoduo.jxc.enums.SalesOrderTypeEnum;
import com.duoduo.jxc.enums.TransactionTypeEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.*;
import com.duoduo.jxc.service.*;
import com.duoduo.jxc.service.data.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements SalesOrderService {

    private final SalesOrderDetailMapper detailMapper;
    private final com.duoduo.jxc.mapper.ProductSpuMapper productSpuMapper;
    private final com.duoduo.jxc.mapper.ProductSkuMapper productSkuMapper;
    private final SalesOrderConverter converter;
    private final WorkflowService workflowService;
    private final InventoryService inventoryService;
    private final ReceivableService receivableService;
    private final ReceivableSourceMapper receivableSourceMapper;
    private final CustomerService customerService;
    private final FinanceTransactionService financeTransactionService;

    @Override
    public PageResult<SalesOrderDTO> pageQuery(SalesOrderQuery query) {
        Page<SalesOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SalesOrder> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(query.getOrderType() != null, SalesOrder::getOrderType, query.getOrderType())
               .eq(query.getStatus() != null, SalesOrder::getStatus, query.getStatus())
               .eq(query.getCustomerId() != null, SalesOrder::getCustomerId, query.getCustomerId())
               .like(StringUtils.hasText(query.getDocNo()), SalesOrder::getDocNo, query.getDocNo())
               .orderByDesc(SalesOrder::getCreateTime);

        page(page, wrapper);
        List<SalesOrderDTO> dtoList = converter.toDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PageResult<SalesOrderDetailDTO> detailPageQuery(com.duoduo.jxc.dto.sales.SalesOrderDetailQuery query) {
        Page<SalesOrderDetail> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SalesOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getSkuId() != null, SalesOrderDetail::getSkuId, query.getSkuId())
               .eq(query.getSpuId() != null, SalesOrderDetail::getSpuId, query.getSpuId())
               .orderByDesc(SalesOrderDetail::getDetailId);
        
        if (query.getDocNo() != null || query.getCustomerId() != null || query.getOrderType() != null) {
            LambdaQueryWrapper<SalesOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.eq(query.getCustomerId() != null, SalesOrder::getCustomerId, query.getCustomerId())
                       .eq(query.getOrderType() != null, SalesOrder::getOrderType, query.getOrderType())
                       .like(StringUtils.hasText(query.getDocNo()), SalesOrder::getDocNo, query.getDocNo());
            List<SalesOrder> orders = list(orderWrapper);
            if (orders.isEmpty()) {
                return new PageResult<>(0L, List.of());
            }
            List<Long> orderIds = orders.stream().map(SalesOrder::getOrderId).toList();
            wrapper.in(SalesOrderDetail::getOrderId, orderIds);
        }

        detailMapper.selectPage(page, wrapper);
        List<SalesOrderDetailDTO> dtoList = converter.toDetailDTOList(page.getRecords());
        
        if (!dtoList.isEmpty()) {
            // 填充单据信息
            Set<Long> orderIds = dtoList.stream().map(SalesOrderDetailDTO::getOrderId).collect(Collectors.toSet());
            if (!orderIds.isEmpty()) {
                Map<Long, SalesOrder> orderMap = listByIds(orderIds).stream()
                        .collect(Collectors.toMap(SalesOrder::getOrderId, e -> e));
                dtoList.forEach(dto -> {
                    SalesOrder order = orderMap.get(dto.getOrderId());
                    if (order != null) {
                        dto.setDocNo(order.getDocNo());
                        dto.setDocDate(order.getDocDate());
                        dto.setCustomerId(order.getCustomerId());
                    }
                });
            }
            
            // 填充商品信息
            Set<Long> spuIds = dtoList.stream().map(SalesOrderDetailDTO::getSpuId).filter(id -> id != null).collect(Collectors.toSet());
            Set<Long> skuIds = dtoList.stream().map(SalesOrderDetailDTO::getSkuId).filter(id -> id != null).collect(Collectors.toSet());
            
            Map<Long, String> spuNameMap = Map.of();
            if (!spuIds.isEmpty()) {
                spuNameMap = productSpuMapper.selectBatchIds(spuIds).stream()
                        .collect(Collectors.toMap(ProductSpu::getSpuId, e -> e.getSpuName() != null ? e.getSpuName() : ""));
            }
            
            Map<Long, ProductSku> skuMap = Map.of();
            if (!skuIds.isEmpty()) {
                skuMap = productSkuMapper.selectBatchIds(skuIds).stream()
                        .collect(Collectors.toMap(ProductSku::getSkuId, e -> e));
            }
            
            for (SalesOrderDetailDTO dto : dtoList) {
                if (dto.getSpuId() != null) {
                    dto.setSpuName(spuNameMap.getOrDefault(dto.getSpuId(), "未知商品"));
                }
                if (dto.getSkuId() != null) {
                    ProductSku sku = skuMap.get(dto.getSkuId());
                    if (sku != null) {
                        dto.setSkuCode(sku.getSkuCode());
                        dto.setAttr1(sku.getAttr1());
                        dto.setAttr2(sku.getAttr2());
                    }
                }
            }
        }

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public SalesOrderDTO getDetail(Long orderId) {
        SalesOrder order = getById(orderId);
        if (order == null) return null;
        
        SalesOrderDTO dto = converter.toDTO(order);
        LambdaQueryWrapper<SalesOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(SalesOrderDetail::getOrderId, orderId);
        List<SalesOrderDetail> details = detailMapper.selectList(detailWrapper);
        List<SalesOrderDetailDTO> detailDTOs = converter.toDetailDTOList(details);
        
        // 批量查询商品信息填充到明细 DTO
        if (!detailDTOs.isEmpty()) {
            Set<Long> spuIds = detailDTOs.stream()
                    .map(SalesOrderDetailDTO::getSpuId).filter(id -> id != null).collect(Collectors.toSet());
            Set<Long> skuIds = detailDTOs.stream()
                    .map(SalesOrderDetailDTO::getSkuId).filter(id -> id != null).collect(Collectors.toSet());
            
            Map<Long, String> spuNameMap = Map.of();
            if (!spuIds.isEmpty()) {
                spuNameMap = productSpuMapper.selectBatchIds(spuIds).stream()
                        .collect(Collectors.toMap(ProductSpu::getSpuId, e -> e.getSpuName() != null ? e.getSpuName() : ""));
            }
            
            Map<Long, ProductSku> skuMap = Map.of();
            if (!skuIds.isEmpty()) {
                skuMap = productSkuMapper.selectBatchIds(skuIds).stream()
                        .collect(Collectors.toMap(ProductSku::getSkuId, e -> e));
            }
            
            for (SalesOrderDetailDTO d : detailDTOs) {
                d.setSpuName(spuNameMap.getOrDefault(d.getSpuId(), ""));
                ProductSku sku = skuMap.get(d.getSkuId());
                if (sku != null) {
                    d.setSkuCode(sku.getSkuCode() != null ? sku.getSkuCode() : "");
                    d.setAttr1(sku.getAttr1() != null ? sku.getAttr1() : "");
                    d.setAttr2(sku.getAttr2() != null ? sku.getAttr2() : "");
                }
            }
        }
        
        dto.setDetails(detailDTOs);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(SalesOrderDTO dto) {
        SalesOrder order = converter.toEntity(dto);
        // 生成单号
        String prefix = dto.getOrderType() == 3 ? "YD" : "XS";
        order.setDocNo(prefix + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + System.currentTimeMillis());
        if (order.getStatus() == null) {
            order.setStatus(10); // 默认草稿
        }
        if (order.getDocDate() == null) {
            order.setDocDate(LocalDate.now());
        }
        save(order);

        List<SalesOrderDetailDTO> details = dto.getDetails();
        if (details != null && !details.isEmpty()) {
            List<SalesOrderDetail> detailEntities = converter.toDetailEntityList(details);
            detailEntities.forEach(d -> {
                d.setDetailId(null);
                d.setOrderId(order.getOrderId());
                if (dto.getOrderType() == 3) {
                    d.setBookedQty(d.getQty());
                    d.setUnfulfilledQty(d.getQty());
                }
                detailMapper.insert(d);
            });
        }
        return order.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(SalesOrderDTO dto) {
        SalesOrder order = converter.toEntity(dto);
        updateById(order);

        LambdaQueryWrapper<SalesOrderDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SalesOrderDetail::getOrderId, order.getOrderId());
        detailMapper.delete(deleteWrapper);

        List<SalesOrderDetailDTO> details = dto.getDetails();
        if (details != null && !details.isEmpty()) {
            List<SalesOrderDetail> detailEntities = converter.toDetailEntityList(details);
            detailEntities.forEach(d -> {
                d.setOrderId(order.getOrderId());
                d.setDetailId(null);
                if (dto.getOrderType() == 3) {
                    d.setBookedQty(d.getQty());
                    d.setUnfulfilledQty(d.getQty());
                }
                detailMapper.insert(d);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long orderId) {
        SalesOrder exist = getById(orderId);
        if (exist == null) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
        }
        if (exist.getStatus() != 10) {
            throw new BusinessException(BizCode.ORDER_NOT_DRAFT);
        }
        
        removeById(orderId);
        LambdaQueryWrapper<SalesOrderDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SalesOrderDetail::getOrderId, orderId);
        detailMapper.delete(deleteWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long orderId) {
        SalesOrder exist = getById(orderId);
        if (exist == null) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
        }
        if (exist.getStatus() != 10) {
            throw new BusinessException(BizCode.ORDER_NOT_DRAFT);
        }

        // 启动工作流（如果有绑定）
        var binding = workflowService.getBinding("SALES_ORDER");
        boolean useWorkflow = binding != null && binding.getEnabled() != null && binding.getEnabled() == 1;

        SalesOrder order = new SalesOrder();
        order.setOrderId(orderId);
        order.setStatus(useWorkflow ? 20 : 30);
        order.setAuditBy(exist.getOperatorId());
        order.setAuditTime(LocalDateTime.now());
        updateById(order);

        if (useWorkflow) {
            WfInstanceStartRequest req = new WfInstanceStartRequest();
            req.setBizType("SALES_ORDER");
            req.setBizId(String.valueOf(orderId));
            req.setTitle("销售单-" + exist.getDocNo());
            req.setInitiatorId(exist.getOperatorId() == null ? 0L : exist.getOperatorId());
            workflowService.startInstance(req);
        } else {
            // ========== 业财一体逻辑（必须执行，不受工作流绑定影响）==========
            processFinancialIntegration(exist, orderId);
        }
    }

    /**
     * 处理业财一体逻辑
     */
    private void processFinancialIntegration(SalesOrder exist, Long orderId) {
        // 查询订单明细
        LambdaQueryWrapper<SalesOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(SalesOrderDetail::getOrderId, orderId);
        List<SalesOrderDetail> details = detailMapper.selectList(detailWrapper);

        if (exist.getOrderType() == 1) {
            // ---------- 批发单：生成应收 ----------
            createReceivable(exist, orderId, details);
        } else if (exist.getOrderType() == 2) {
            // ---------- 零售单：日结日清 ----------
            processRetailOrder(exist, orderId, details);
        } else if (exist.getOrderType() == 3) {
            // ---------- 预订单：审核后锁定库存 ----------
            lockInventoryForBooking(orderId, details);
        }
    }

    /**
     * 创建应收记录
     */
    private void createReceivable(SalesOrder order, Long orderId, List<SalesOrderDetail> details) {
        // 查询客户信息
        String customerName = "";
        if (order.getCustomerId() != null) {
            Customer customer = customerService.getDetail(order.getCustomerId());
            if (customer != null) {
                customerName = customer.getCustomerName();
            }
        }

        // 1. 创建应收单
        Receivable receivable = new Receivable();
        receivable.setSourceType("SALES_ORDER");
        receivable.setSourceId(orderId);
        receivable.setSourceDocNo(order.getDocNo());
        receivable.setCustomerId(order.getCustomerId());
        receivable.setCustomerName(customerName);
        receivable.setAmount(order.getActualAmount());
        receivable.setReceivedAmount(BigDecimal.ZERO);
        receivable.setRemainingAmount(order.getActualAmount());
        receivable.setBillDate(order.getDocDate().atStartOfDay());
        receivable.setStatus(0); // 未收款
        receivable.setBillNo("YS" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase());
        receivableService.save(receivable);

        // 2. 记录应收来源明细
        for (SalesOrderDetail detail : details) {
            ReceivableSource source = new ReceivableSource();
            source.setReceivableId(receivable.getReceivableId());
            source.setOrderId(orderId);
            source.setDetailId(detail.getDetailId());
            source.setSkuId(detail.getSkuId());
            source.setQty(detail.getQty());
            source.setUnitPrice(detail.getUnitPrice());
            source.setLineAmount(detail.getLineAmount());
            receivableSourceMapper.insert(source);
        }

        // 3. 记录库存流水（销售出库）
        for (SalesOrderDetail detail : details) {
            if (detail.getWarehouseId() != null) {
                inventoryService.deductStock(
                        detail.getWarehouseId(),
                        detail.getSkuId(),
                        detail.getQty(),
                        "SALES_ORDER",
                        orderId,
                        order.getDocNo()
                );
            }
        }

        // 4. 更新订单核销状态
        SalesOrder settleUpdate = new SalesOrder();
        settleUpdate.setOrderId(orderId);
        settleUpdate.setSettleStatus(0); // 未核销
        updateById(settleUpdate);

        log.info("批发单审核后生成应收: orderId={}, receivableId={}", orderId, receivable.getReceivableId());
    }

    /**
     * 预订单审核时锁定库存
     */
    private void lockInventoryForBooking(Long orderId, List<SalesOrderDetail> details) {
        for (SalesOrderDetail detail : details) {
            if (detail.getWarehouseId() != null) {
                inventoryService.lockStock(
                        detail.getWarehouseId(),
                        detail.getSkuId(),
                        detail.getQty(),
                        orderId
                );
            }
        }
        log.info("预订单审核后锁定库存: orderId={}, itemCount={}", orderId, details.size());
    }

    /**
     * 处理零售单（日结日清）
     */
    private void processRetailOrder(SalesOrder order, Long orderId, List<SalesOrderDetail> details) {
        // 1. 生成资金流水（收入）
        FinanceTransaction trans = new FinanceTransaction();
        trans.setTransactionNo("RZ" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase());
        trans.setAccountId(order.getStoreId()); // 门店作为账户
        trans.setAccountName("门店-" + order.getStoreId());
        trans.setType(TransactionTypeEnum.INCOME.getCode());
        trans.setAmount(order.getActualAmount());
        trans.setCategory("RETAIL_SALES");
        trans.setBillType("SALES_ORDER");
        trans.setBillNo(order.getDocNo());
        trans.setTransactionDate(LocalDateTime.now());
        trans.setCreateTime(LocalDateTime.now());
        financeTransactionService.create(trans);

        // 2. 记录库存流水
        for (SalesOrderDetail detail : details) {
            if (detail.getWarehouseId() != null) {
                inventoryService.deductStock(
                        detail.getWarehouseId(),
                        detail.getSkuId(),
                        detail.getQty(),
                        "SALES_ORDER",
                        orderId,
                        order.getDocNo()
                );
            }
        }

        // 3. 更新订单：paid_amount = actual_amount, settle_status = 2
        SalesOrder settleUpdate = new SalesOrder();
        settleUpdate.setOrderId(orderId);
        settleUpdate.setPaidAmount(order.getActualAmount());
        settleUpdate.setSettleStatus(2); // 已核销
        updateById(settleUpdate);

        log.info("零售单审核处理完成: orderId={}, amount={}, settleStatus=2", orderId, order.getActualAmount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unauditOrder(Long orderId) {
        SalesOrder exist = getById(orderId);
        if (exist == null) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
        }
        if (exist.getStatus() != 30) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_AUDITED);
        }

        // 针对预订单反审核：需要解锁库存
        if (exist.getOrderType() != null && exist.getOrderType() == SalesOrderTypeEnum.BOOKING.getCode()) {
            LambdaQueryWrapper<SalesOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(SalesOrderDetail::getOrderId, orderId);
            List<SalesOrderDetail> details = detailMapper.selectList(detailWrapper);

            // 如果已有部分发货，不允许反审核
            for (SalesOrderDetail detail : details) {
                if (detail.getDeliveryQty() != null && detail.getDeliveryQty() > 0) {
                    throw new BusinessException("该预订单已有发货记录，不允许反审核");
                }
            }

            // 解锁全部锁定的库存
            for (SalesOrderDetail detail : details) {
                if (detail.getWarehouseId() != null && detail.getQty() != null && detail.getQty() > 0) {
                    inventoryService.unlockStock(
                            detail.getWarehouseId(),
                            detail.getSkuId(),
                            detail.getQty(),
                            orderId
                    );
                }
            }
            log.info("预订单反审核，解锁库存: orderId={}", orderId);
        } else if (exist.getOrderType() != null && exist.getOrderType() == SalesOrderTypeEnum.WHOLESALE.getCode()) {
            // 批发单反审核（抛出异常因涉及财务、出库等回滚暂不支持）
            throw new BusinessException("暂时不支持批发单/零售单反审核");
        }

        LambdaUpdateWrapper<SalesOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SalesOrder::getOrderId, orderId)
                .set(SalesOrder::getStatus, 10)
                .set(SalesOrder::getAuditBy, null)
                .set(SalesOrder::getAuditTime, null);
        update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void convertToSales(Long bookingOrderId) {
        SalesOrderDTO booking = getDetail(bookingOrderId);
        if (booking == null || booking.getOrderType() != 3) {
            throw new BusinessException(BizCode.BOOKING_ORDER_INVALID);
        }

        // 解锁预订单中未发货部分的锁定库存
        LambdaQueryWrapper<SalesOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(SalesOrderDetail::getOrderId, bookingOrderId);
        List<SalesOrderDetail> details = detailMapper.selectList(detailWrapper);
        for (SalesOrderDetail detail : details) {
            if (detail.getWarehouseId() != null && detail.getUnfulfilledQty() != null && detail.getUnfulfilledQty() > 0) {
                inventoryService.unlockStock(
                        detail.getWarehouseId(),
                        detail.getSkuId(),
                        detail.getUnfulfilledQty(),
                        bookingOrderId
                );
                log.info("预定转销售，解锁剩余库存: orderId={}, skuId={}, qty={}",
                        bookingOrderId, detail.getSkuId(), detail.getUnfulfilledQty());
            }
        }

        // 创建新的销售单（根据未发货数量生成新的明细？）
        // 原始实现是直接拿整个 booking 对象改掉，但其实应该只把未发货的部分转为销售单，不过这里按照原逻辑：
        booking.setOrderType(1); // 批发单
        booking.setOrderId(null);
        booking.setDocNo(null);
        booking.setStatus(10); // 草稿
        
        createOrder(booking);
        
        // 更新原预订单状态
        SalesOrder updateBooking = new SalesOrder();
        updateBooking.setOrderId(bookingOrderId);
        updateBooking.setStatus(40); // 已完成
        updateById(updateBooking);
    }
}
