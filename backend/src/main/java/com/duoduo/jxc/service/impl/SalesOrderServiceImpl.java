package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.SalesOrderConverter;
import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.dto.sales.SalesOrderDetailDTO;
import com.duoduo.jxc.dto.sales.SalesOrderQuery;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.entity.SalesOrderDetail;
import com.duoduo.jxc.dto.workflow.WfInstanceStartRequest;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SalesOrderDetailMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.SalesOrderService;
import com.duoduo.jxc.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl extends ServiceImpl<SalesOrderMapper, SalesOrder> implements SalesOrderService {

    private final SalesOrderDetailMapper detailMapper;
    private final SalesOrderConverter converter;
    private final WorkflowService workflowService;

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
        dto.setDetails(converter.toDetailDTOList(details));
        
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

        SalesOrder order = new SalesOrder();
        order.setOrderId(orderId);
        order.setStatus(30);
        order.setAuditBy(exist.getOperatorId());
        order.setAuditTime(LocalDateTime.now());
        updateById(order);

        var binding = workflowService.getBinding("SALES_ORDER");
        if (binding == null || binding.getEnabled() == null || binding.getEnabled() == 0) {
            return;
        }

        WfInstanceStartRequest req = new WfInstanceStartRequest();
        req.setBizType("SALES_ORDER");
        req.setBizId(String.valueOf(orderId));
        req.setTitle("销售单-" + exist.getDocNo());
        req.setInitiatorId(exist.getOperatorId() == null ? 0L : exist.getOperatorId());
        workflowService.startInstance(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unauditOrder(Long orderId) {
        SalesOrder exist = getById(orderId);
        if (exist == null) {
            throw new BusinessException(BizCode.SALES_ORDER_NOT_FOUND);
        }
        if (exist.getStatus() != 30) {
            throw new BusinessException(BizCode.BAD_REQUEST.getCode(), "只有已审核的单据才能反审核");
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
        // 创建新的销售单
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
