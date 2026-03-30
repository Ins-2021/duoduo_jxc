package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ReworkOrderDTO;
import com.duoduo.jxc.dto.ReworkOrderQuery;
import com.duoduo.jxc.entity.ReworkOrder;
import com.duoduo.jxc.mapper.ReworkOrderMapper;
import com.duoduo.jxc.service.ReworkOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReworkOrderServiceImpl extends ServiceImpl<ReworkOrderMapper, ReworkOrder> implements ReworkOrderService {

    @Override
    public PageResult<ReworkOrderDTO> pageQuery(ReworkOrderQuery query) {
        Page<ReworkOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ReworkOrder> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getReworkNo()), ReworkOrder::getReworkNo, query.getReworkNo());
        wrapper.eq(query.getCheckId() != null, ReworkOrder::getCheckId, query.getCheckId());
        wrapper.eq(query.getBundleId() != null, ReworkOrder::getBundleId, query.getBundleId());
        wrapper.eq(StringUtils.hasText(query.getStatus()), ReworkOrder::getStatus, query.getStatus());

        page(page, wrapper);

        List<ReworkOrderDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ReworkOrderDTO getDetail(Long id) {
        ReworkOrder entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ReworkOrderDTO dto) {
        ReworkOrder entity = new ReworkOrder();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getReworkId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReworkOrderDTO dto) {
        ReworkOrder entity = new ReworkOrder();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public ReworkOrderDTO getByCheckId(Long checkId) {
        LambdaQueryWrapper<ReworkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReworkOrder::getCheckId, checkId);
        ReworkOrder entity = getOne(wrapper);
        return entity != null ? convertToDTO(entity) : null;
    }

    private ReworkOrderDTO convertToDTO(ReworkOrder entity) {
        ReworkOrderDTO dto = new ReworkOrderDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
