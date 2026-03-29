package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.QualityCheckDTO;
import com.duoduo.jxc.dto.QualityCheckQuery;
import com.duoduo.jxc.entity.QualityCheck;
import com.duoduo.jxc.mapper.QualityCheckMapper;
import com.duoduo.jxc.service.QualityCheckService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualityCheckServiceImpl extends ServiceImpl<QualityCheckMapper, QualityCheck> implements QualityCheckService {

    @Override
    public PageResult<QualityCheckDTO> pageQuery(QualityCheckQuery query) {
        Page<QualityCheck> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<QualityCheck> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getCheckNo()), QualityCheck::getCheckNo, query.getCheckNo());
        wrapper.eq(query.getBundleId() != null, QualityCheck::getBundleId, query.getBundleId());
        wrapper.eq(query.getProcessId() != null, QualityCheck::getProcessId, query.getProcessId());
        wrapper.like(StringUtils.hasText(query.getResult()), QualityCheck::getResult, query.getResult());
        wrapper.eq(query.getCheckerId() != null, QualityCheck::getCheckerId, query.getCheckerId());

        page(page, wrapper);

        List<QualityCheckDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public QualityCheckDTO getDetail(Long id) {
        QualityCheck entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(QualityCheckDTO dto) {
        QualityCheck entity = new QualityCheck();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getCheckId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QualityCheckDTO dto) {
        QualityCheck entity = new QualityCheck();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private QualityCheckDTO convertToDTO(QualityCheck entity) {
        QualityCheckDTO dto = new QualityCheckDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
