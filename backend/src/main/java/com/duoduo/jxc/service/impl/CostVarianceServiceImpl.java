package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostVarianceDTO;
import com.duoduo.jxc.dto.cost.CostVarianceQuery;
import com.duoduo.jxc.entity.CostVariance;
import com.duoduo.jxc.mapper.CostVarianceMapper;
import com.duoduo.jxc.service.CostVarianceService;
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
public class CostVarianceServiceImpl extends ServiceImpl<CostVarianceMapper, CostVariance> implements CostVarianceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<CostVarianceDTO> pageQuery(CostVarianceQuery query) {
        Page<CostVariance> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CostVariance> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getVarianceNo()), CostVariance::getVarianceNo, query.getVarianceNo())
               .eq(StringUtils.hasText(query.getAccountPeriod()), CostVariance::getAccountPeriod, query.getAccountPeriod())
               .eq(query.getProductionId() != null, CostVariance::getProductionId, query.getProductionId())
               .eq(query.getStyleId() != null, CostVariance::getStyleId, query.getStyleId())
               .like(StringUtils.hasText(query.getStyleCode()), CostVariance::getStyleCode, query.getStyleCode())
               .eq(query.getStatus() != null, CostVariance::getStatus, query.getStatus())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(CostVariance::getVarianceNo, query.getKeyword())
                    .or().like(CostVariance::getStyleCode, query.getKeyword())
                    .or().like(CostVariance::getStyleName, query.getKeyword()))
               .orderByDesc(CostVariance::getCreateTime);
        page(page, wrapper);
        List<CostVarianceDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CostVarianceDTO getDetail(Long id) {
        CostVariance entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CostVarianceDTO dto) {
        CostVariance entity = new CostVariance();
        BeanUtils.copyProperties(dto, entity);
        entity.setVarianceNo(generateVarianceNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getVarianceId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CostVarianceDTO dto) {
        CostVariance entity = new CostVariance();
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
    public void analyze(Long id) {
        CostVariance entity = new CostVariance();
        entity.setVarianceId(id);
        entity.setStatus(1);
        entity.setAnalyzeTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private CostVarianceDTO toDTO(CostVariance entity) {
        CostVarianceDTO dto = new CostVarianceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateVarianceNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "CV" + dateStr + randomStr;
    }
}
