package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.PatrolCheckDTO;
import com.duoduo.jxc.dto.PatrolCheckQuery;
import com.duoduo.jxc.entity.PatrolCheck;
import com.duoduo.jxc.mapper.PatrolCheckMapper;
import com.duoduo.jxc.service.PatrolCheckService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatrolCheckServiceImpl extends ServiceImpl<PatrolCheckMapper, PatrolCheck> implements PatrolCheckService {

    @Override
    public PageResult<PatrolCheckDTO> pageQuery(PatrolCheckQuery query) {
        Page<PatrolCheck> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PatrolCheck> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getPatrolNo()), PatrolCheck::getPatrolNo, query.getPatrolNo());
        wrapper.eq(query.getWorkshopId() != null, PatrolCheck::getWorkshopId, query.getWorkshopId());
        wrapper.eq(query.getInspectorId() != null, PatrolCheck::getInspectorId, query.getInspectorId());
        wrapper.eq(StringUtils.hasText(query.getCheckType()), PatrolCheck::getCheckType, query.getCheckType());
        wrapper.eq(StringUtils.hasText(query.getResult()), PatrolCheck::getResult, query.getResult());

        page(page, wrapper);

        List<PatrolCheckDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PatrolCheckDTO getDetail(Long id) {
        PatrolCheck entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PatrolCheckDTO dto) {
        PatrolCheck entity = new PatrolCheck();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getPatrolId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PatrolCheckDTO dto) {
        PatrolCheck entity = new PatrolCheck();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private PatrolCheckDTO convertToDTO(PatrolCheck entity) {
        PatrolCheckDTO dto = new PatrolCheckDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
