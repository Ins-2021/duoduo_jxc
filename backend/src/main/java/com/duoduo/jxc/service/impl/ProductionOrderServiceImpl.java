package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.entity.ProductionOrder;
import com.duoduo.jxc.mapper.ProductionOrderMapper;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder> implements ProductionOrderService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<ProductionOrderDTO> pageQuery(ProductionOrderQuery query) {
        Page<ProductionOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getOrderNo()), ProductionOrder::getOrderNo, query.getOrderNo())
               .like(StringUtils.hasText(query.getStyleNo()), ProductionOrder::getStyleNo, query.getStyleNo())
               .eq(query.getStyleId() != null, ProductionOrder::getStyleId, query.getStyleId())
               .eq(query.getCustomerId() != null, ProductionOrder::getCustomerId, query.getCustomerId())
               .eq(query.getFactoryId() != null, ProductionOrder::getFactoryId, query.getFactoryId())
               .eq(StringUtils.hasText(query.getStatus()), ProductionOrder::getStatus, query.getStatus())
               .eq(StringUtils.hasText(query.getPriority()), ProductionOrder::getPriority, query.getPriority())
               .ge(query.getDeadlineFrom() != null, ProductionOrder::getDeadline, query.getDeadlineFrom())
               .le(query.getDeadlineTo() != null, ProductionOrder::getDeadline, query.getDeadlineTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(ProductionOrder::getOrderNo, query.getKeyword())
                    .or().like(ProductionOrder::getStyleNo, query.getKeyword())
                    .or().like(ProductionOrder::getStyleName, query.getKeyword()))
               .orderByDesc(ProductionOrder::getCreateTime);
        page(page, wrapper);
        List<ProductionOrderDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProductionOrderDTO getDetail(Long id) {
        ProductionOrder entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductionOrderDTO dto) {
        ProductionOrder entity = new ProductionOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setOrderNo(generateOrderNo());
        if (entity.getStatus() == null) {
            entity.setStatus("pending");
        }
        if (entity.getCompletedQuantity() == null) {
            entity.setCompletedQuantity(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductionOrderDTO dto) {
        ProductionOrder entity = new ProductionOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        ProductionOrder entity = new ProductionOrder();
        entity.setOrderId(id);
        entity.setStatus(status);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private ProductionOrderDTO toDTO(ProductionOrder entity) {
        ProductionOrderDTO dto = new ProductionOrderDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateOrderNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PO" + dateStr + randomStr;
    }
}
