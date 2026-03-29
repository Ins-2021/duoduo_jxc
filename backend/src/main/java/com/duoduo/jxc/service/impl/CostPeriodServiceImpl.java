package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostPeriodDTO;
import com.duoduo.jxc.dto.cost.CostPeriodQuery;
import com.duoduo.jxc.entity.CostPeriod;
import com.duoduo.jxc.mapper.CostPeriodMapper;
import com.duoduo.jxc.service.CostPeriodService;
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
public class CostPeriodServiceImpl extends ServiceImpl<CostPeriodMapper, CostPeriod> implements CostPeriodService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<CostPeriodDTO> pageQuery(CostPeriodQuery query) {
        Page<CostPeriod> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CostPeriod> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPeriodNo()), CostPeriod::getPeriodNo, query.getPeriodNo())
               .eq(StringUtils.hasText(query.getYearMonth()), CostPeriod::getYearMonth, query.getYearMonth())
               .eq(query.getStatus() != null, CostPeriod::getStatus, query.getStatus())
               .ge(query.getStartDateFrom() != null, CostPeriod::getStartDate, query.getStartDateFrom())
               .le(query.getStartDateTo() != null, CostPeriod::getStartDate, query.getStartDateTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(CostPeriod::getPeriodNo, query.getKeyword())
                    .or().like(CostPeriod::getYearMonth, query.getKeyword()))
               .orderByDesc(CostPeriod::getCreateTime);
        page(page, wrapper);
        List<CostPeriodDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CostPeriodDTO getDetail(Long id) {
        CostPeriod entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CostPeriodDTO dto) {
        CostPeriod entity = new CostPeriod();
        BeanUtils.copyProperties(dto, entity);
        entity.setPeriodNo(generatePeriodNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getPeriodId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CostPeriodDTO dto) {
        CostPeriod entity = new CostPeriod();
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
    public void updateStatus(Long id, Integer status) {
        CostPeriod entity = new CostPeriod();
        entity.setPeriodId(id);
        entity.setStatus(status);
        entity.setUpdateTime(LocalDateTime.now());
        if (status == 1) {
            entity.setCalculateTime(LocalDateTime.now());
        } else if (status == 2) {
            entity.setCloseTime(LocalDateTime.now());
        }
        updateById(entity);
    }

    private CostPeriodDTO toDTO(CostPeriod entity) {
        CostPeriodDTO dto = new CostPeriodDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generatePeriodNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "CP" + dateStr + randomStr;
    }
}
