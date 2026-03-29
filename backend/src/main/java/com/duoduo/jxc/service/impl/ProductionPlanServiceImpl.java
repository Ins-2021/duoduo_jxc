package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.ProductionPlanDTO;
import com.duoduo.jxc.dto.production.ProductionPlanQuery;
import com.duoduo.jxc.entity.ProductionPlan;
import com.duoduo.jxc.mapper.ProductionPlanMapper;
import com.duoduo.jxc.service.ProductionPlanService;
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
public class ProductionPlanServiceImpl extends ServiceImpl<ProductionPlanMapper, ProductionPlan> implements ProductionPlanService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<ProductionPlanDTO> pageQuery(ProductionPlanQuery query) {
        Page<ProductionPlan> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProductionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPlanNo()), ProductionPlan::getPlanNo, query.getPlanNo())
               .eq(query.getOrderId() != null, ProductionPlan::getOrderId, query.getOrderId())
               .eq(StringUtils.hasText(query.getStatus()), ProductionPlan::getStatus, query.getStatus())
               .ge(query.getPlannedDateFrom() != null, ProductionPlan::getPlannedDate, query.getPlannedDateFrom())
               .le(query.getPlannedDateTo() != null, ProductionPlan::getPlannedDate, query.getPlannedDateTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(ProductionPlan::getPlanNo, query.getKeyword()))
               .orderByDesc(ProductionPlan::getCreateTime);
        page(page, wrapper);
        List<ProductionPlanDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProductionPlanDTO getDetail(Long id) {
        ProductionPlan entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductionPlanDTO dto) {
        ProductionPlan entity = new ProductionPlan();
        BeanUtils.copyProperties(dto, entity);
        entity.setPlanNo(generatePlanNo());
        if (entity.getStatus() == null) {
            entity.setStatus("draft");
        }
        if (entity.getActualQuantity() == null) {
            entity.setActualQuantity(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        save(entity);
        return entity.getPlanId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductionPlanDTO dto) {
        ProductionPlan entity = new ProductionPlan();
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
        ProductionPlan entity = new ProductionPlan();
        entity.setPlanId(id);
        entity.setStatus(status);
        updateById(entity);
    }

    private ProductionPlanDTO toDTO(ProductionPlan entity) {
        ProductionPlanDTO dto = new ProductionPlanDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generatePlanNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PP" + dateStr + randomStr;
    }
}
