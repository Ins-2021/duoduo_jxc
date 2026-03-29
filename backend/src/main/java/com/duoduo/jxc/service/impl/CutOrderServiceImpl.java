package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.CutOrderDTO;
import com.duoduo.jxc.dto.production.CutOrderQuery;
import com.duoduo.jxc.entity.CutOrder;
import com.duoduo.jxc.mapper.CutOrderMapper;
import com.duoduo.jxc.service.CutOrderService;
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
public class CutOrderServiceImpl extends ServiceImpl<CutOrderMapper, CutOrder> implements CutOrderService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<CutOrderDTO> pageQuery(CutOrderQuery query) {
        Page<CutOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CutOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPlanNo()), CutOrder::getPlanNo, query.getPlanNo())
               .eq(query.getOrderId() != null, CutOrder::getOrderId, query.getOrderId())
               .eq(query.getFabricId() != null, CutOrder::getFabricId, query.getFabricId())
               .eq(StringUtils.hasText(query.getStatus()), CutOrder::getStatus, query.getStatus())
               .ge(query.getCuttingDateFrom() != null, CutOrder::getCuttingDate, query.getCuttingDateFrom())
               .le(query.getCuttingDateTo() != null, CutOrder::getCuttingDate, query.getCuttingDateTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(CutOrder::getPlanNo, query.getKeyword()))
               .orderByDesc(CutOrder::getCreateTime);
        page(page, wrapper);
        List<CutOrderDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CutOrderDTO getDetail(Long id) {
        CutOrder entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CutOrderDTO dto) {
        CutOrder entity = new CutOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setPlanNo(generatePlanNo());
        if (entity.getStatus() == null) {
            entity.setStatus("pending");
        }
        entity.setCreateTime(LocalDateTime.now());
        save(entity);
        return entity.getPlanId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CutOrderDTO dto) {
        CutOrder entity = new CutOrder();
        BeanUtils.copyProperties(dto, entity);
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
        CutOrder entity = new CutOrder();
        entity.setPlanId(id);
        entity.setStatus(status);
        if ("completed".equals(status)) {
            entity.setCompletedAt(LocalDateTime.now());
        }
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeCutting(Long id, Integer cutQuantity) {
        CutOrder entity = new CutOrder();
        entity.setPlanId(id);
        entity.setCutQuantity(cutQuantity);
        entity.setStatus("completed");
        entity.setCompletedAt(LocalDateTime.now());
        updateById(entity);
    }

    private CutOrderDTO toDTO(CutOrder entity) {
        CutOrderDTO dto = new CutOrderDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generatePlanNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "CO" + dateStr + randomStr;
    }
}
