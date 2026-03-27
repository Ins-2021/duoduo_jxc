package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.PurchaseOrderConverter;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderDetailDTO;
import com.duoduo.jxc.dto.purchase.PurchaseOrderQuery;
import com.duoduo.jxc.entity.PurchaseOrder;
import com.duoduo.jxc.entity.PurchaseOrderDetail;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PurchaseOrderDetailMapper;
import com.duoduo.jxc.mapper.PurchaseOrderMapper;
import com.duoduo.jxc.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    private final PurchaseOrderDetailMapper detailMapper;
    private final PurchaseOrderConverter converter;

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
        PurchaseOrder order = converter.toEntity(dto);
        // 生成单号
        String prefix = dto.getOrderType() == 1 ? "JH" : "JY";
        order.setDocNo(prefix + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + System.currentTimeMillis());
        if (order.getStatus() == null) {
            order.setStatus(10); // 默认草稿
        }
        if (order.getDocDate() == null) {
            order.setDocDate(LocalDate.now());
        }
        
        // 新增金额兜底处理
        if (order.getDiscountAmount() == null) order.setDiscountAmount(java.math.BigDecimal.ZERO);
        if (order.getOtherFee() == null) order.setOtherFee(java.math.BigDecimal.ZERO);
        if (order.getActualAmount() == null) {
            java.math.BigDecimal actual = (order.getTotalAmount() != null ? order.getTotalAmount() : java.math.BigDecimal.ZERO)
                    .subtract(order.getDiscountAmount())
                    .add(order.getOtherFee());
            order.setActualAmount(actual);
        }
        
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
        PurchaseOrder order = converter.toEntity(dto);
        
        // 新增金额兜底处理
        if (order.getDiscountAmount() == null) order.setDiscountAmount(java.math.BigDecimal.ZERO);
        if (order.getOtherFee() == null) order.setOtherFee(java.math.BigDecimal.ZERO);
        if (order.getActualAmount() == null) {
            java.math.BigDecimal actual = (order.getTotalAmount() != null ? order.getTotalAmount() : java.math.BigDecimal.ZERO)
                    .subtract(order.getDiscountAmount())
                    .add(order.getOtherFee());
            order.setActualAmount(actual);
        }
        
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
        if (exist.getStatus() != 10) {
            throw new BusinessException(BizCode.ORDER_NOT_DRAFT);
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderId(orderId);
        order.setStatus(30);
        updateById(order);
    }
}
