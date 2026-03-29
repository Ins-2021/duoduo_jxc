package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostSummaryDTO;
import com.duoduo.jxc.dto.cost.CostSummaryQuery;
import com.duoduo.jxc.entity.CostSummary;
import com.duoduo.jxc.mapper.CostSummaryMapper;
import com.duoduo.jxc.service.CostSummaryService;
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
public class CostSummaryServiceImpl extends ServiceImpl<CostSummaryMapper, CostSummary> implements CostSummaryService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<CostSummaryDTO> pageQuery(CostSummaryQuery query) {
        Page<CostSummary> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CostSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getSummaryNo()), CostSummary::getSummaryNo, query.getSummaryNo())
               .eq(StringUtils.hasText(query.getAccountPeriod()), CostSummary::getAccountPeriod, query.getAccountPeriod())
               .eq(query.getProductionId() != null, CostSummary::getProductionId, query.getProductionId())
               .eq(query.getStyleId() != null, CostSummary::getStyleId, query.getStyleId())
               .like(StringUtils.hasText(query.getStyleCode()), CostSummary::getStyleCode, query.getStyleCode())
               .eq(query.getStatus() != null, CostSummary::getStatus, query.getStatus())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(CostSummary::getSummaryNo, query.getKeyword())
                    .or().like(CostSummary::getStyleCode, query.getKeyword())
                    .or().like(CostSummary::getStyleName, query.getKeyword()))
               .orderByDesc(CostSummary::getCreateTime);
        page(page, wrapper);
        List<CostSummaryDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CostSummaryDTO getDetail(Long id) {
        CostSummary entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CostSummaryDTO dto) {
        CostSummary entity = new CostSummary();
        BeanUtils.copyProperties(dto, entity);
        entity.setSummaryNo(generateSummaryNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getSummaryId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CostSummaryDTO dto) {
        CostSummary entity = new CostSummary();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private CostSummaryDTO toDTO(CostSummary entity) {
        CostSummaryDTO dto = new CostSummaryDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateSummaryNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "CS" + dateStr + randomStr;
    }
}
