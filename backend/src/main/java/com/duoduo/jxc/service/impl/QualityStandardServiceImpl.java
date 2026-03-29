package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.QualityStandardDTO;
import com.duoduo.jxc.dto.QualityStandardQuery;
import com.duoduo.jxc.entity.QualityStandard;
import com.duoduo.jxc.mapper.QualityStandardMapper;
import com.duoduo.jxc.service.QualityStandardService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualityStandardServiceImpl extends ServiceImpl<QualityStandardMapper, QualityStandard> implements QualityStandardService {

    @Override
    public PageResult<QualityStandardDTO> pageQuery(QualityStandardQuery query) {
        Page<QualityStandard> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<QualityStandard> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getStandardName()), QualityStandard::getStandardName, query.getStandardName());
        wrapper.like(StringUtils.hasText(query.getStandardType()), QualityStandard::getStandardType, query.getStandardType());
        wrapper.eq(query.getOrderId() != null, QualityStandard::getOrderId, query.getOrderId());
        wrapper.eq(query.getProcessId() != null, QualityStandard::getProcessId, query.getProcessId());

        page(page, wrapper);

        List<QualityStandardDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public QualityStandardDTO getDetail(Long id) {
        QualityStandard entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(QualityStandardDTO dto) {
        QualityStandard entity = new QualityStandard();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getStandardId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QualityStandardDTO dto) {
        QualityStandard entity = new QualityStandard();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private QualityStandardDTO convertToDTO(QualityStandard entity) {
        QualityStandardDTO dto = new QualityStandardDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
