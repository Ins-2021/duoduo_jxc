package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.ProductionScheduleDTO;
import com.duoduo.jxc.dto.production.ProductionScheduleQuery;
import com.duoduo.jxc.entity.ProductionSchedule;
import com.duoduo.jxc.mapper.ProductionScheduleMapper;
import com.duoduo.jxc.service.ProductionScheduleService;
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
public class ProductionScheduleServiceImpl extends ServiceImpl<ProductionScheduleMapper, ProductionSchedule> implements ProductionScheduleService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<ProductionScheduleDTO> pageQuery(ProductionScheduleQuery query) {
        Page<ProductionSchedule> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProductionSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getScheduleNo()), ProductionSchedule::getScheduleNo, query.getScheduleNo())
               .eq(query.getOrderId() != null, ProductionSchedule::getOrderId, query.getOrderId())
               .eq(query.getPlanId() != null, ProductionSchedule::getPlanId, query.getPlanId())
               .eq(StringUtils.hasText(query.getStatus()), ProductionSchedule::getStatus, query.getStatus())
               .ge(query.getStartDateFrom() != null, ProductionSchedule::getStartDate, query.getStartDateFrom())
               .le(query.getStartDateTo() != null, ProductionSchedule::getStartDate, query.getStartDateTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(ProductionSchedule::getScheduleNo, query.getKeyword()))
               .orderByDesc(ProductionSchedule::getCreateTime);
        page(page, wrapper);
        List<ProductionScheduleDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProductionScheduleDTO getDetail(Long id) {
        ProductionSchedule entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductionScheduleDTO dto) {
        ProductionSchedule entity = new ProductionSchedule();
        BeanUtils.copyProperties(dto, entity);
        entity.setScheduleNo(generateScheduleNo());
        if (entity.getStatus() == null) {
            entity.setStatus("draft");
        }
        entity.setCreateTime(LocalDateTime.now());
        save(entity);
        return entity.getScheduleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductionScheduleDTO dto) {
        ProductionSchedule entity = new ProductionSchedule();
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
        ProductionSchedule entity = new ProductionSchedule();
        entity.setScheduleId(id);
        entity.setStatus(status);
        updateById(entity);
    }

    private ProductionScheduleDTO toDTO(ProductionSchedule entity) {
        ProductionScheduleDTO dto = new ProductionScheduleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateScheduleNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PS" + dateStr + randomStr;
    }
}
