package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.StandardCostDTO;
import com.duoduo.jxc.dto.cost.StandardCostQuery;
import com.duoduo.jxc.entity.StandardCost;
import com.duoduo.jxc.mapper.StandardCostMapper;
import com.duoduo.jxc.service.StandardCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandardCostServiceImpl extends ServiceImpl<StandardCostMapper, StandardCost> implements StandardCostService {

    @Override
    public PageResult<StandardCostDTO> pageQuery(StandardCostQuery query) {
        Page<StandardCost> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<StandardCost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getStyleId() != null, StandardCost::getStyleId, query.getStyleId())
               .like(StringUtils.hasText(query.getStyleCode()), StandardCost::getStyleCode, query.getStyleCode())
               .eq(query.getIsCurrent() != null, StandardCost::getIsCurrent, query.getIsCurrent())
               .eq(StringUtils.hasText(query.getVersionNo()), StandardCost::getVersionNo, query.getVersionNo())
               .ge(query.getEffectiveDateFrom() != null, StandardCost::getEffectiveDate, query.getEffectiveDateFrom())
               .le(query.getEffectiveDateTo() != null, StandardCost::getEffectiveDate, query.getEffectiveDateTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(StandardCost::getStyleCode, query.getKeyword())
                    .or().like(StandardCost::getStyleName, query.getKeyword())
                    .or().like(StandardCost::getVersionNo, query.getKeyword()))
               .orderByDesc(StandardCost::getCreateTime);
        page(page, wrapper);
        List<StandardCostDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public StandardCostDTO getDetail(Long id) {
        StandardCost entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StandardCostDTO dto) {
        StandardCost entity = new StandardCost();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getIsCurrent() == null) {
            entity.setIsCurrent(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getCostId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StandardCostDTO dto) {
        StandardCost entity = new StandardCost();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private StandardCostDTO toDTO(StandardCost entity) {
        StandardCostDTO dto = new StandardCostDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
