package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.BundleDTO;
import com.duoduo.jxc.dto.BundleQuery;
import com.duoduo.jxc.entity.Bundle;
import com.duoduo.jxc.mapper.BundleMapper;
import com.duoduo.jxc.service.BundleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BundleServiceImpl extends ServiceImpl<BundleMapper, Bundle> implements BundleService {

    @Override
    public PageResult<BundleDTO> pageQuery(BundleQuery query) {
        Page<Bundle> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Bundle> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getBundleNo()), Bundle::getBundleNo, query.getBundleNo());
        wrapper.eq(query.getOrderId() != null, Bundle::getOrderId, query.getOrderId());
        wrapper.like(StringUtils.hasText(query.getStatus()), Bundle::getStatus, query.getStatus());

        page(page, wrapper);

        List<BundleDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public BundleDTO getDetail(Long id) {
        Bundle entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BundleDTO dto) {
        Bundle entity = new Bundle();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getBundleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BundleDTO dto) {
        Bundle entity = new Bundle();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private BundleDTO convertToDTO(Bundle entity) {
        BundleDTO dto = new BundleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
