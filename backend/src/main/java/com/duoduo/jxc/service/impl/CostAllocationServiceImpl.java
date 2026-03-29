package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostAllocationDTO;
import com.duoduo.jxc.dto.cost.CostAllocationQuery;
import com.duoduo.jxc.entity.cost.CostAllocation;
import com.duoduo.jxc.mapper.CostAllocationMapper;
import com.duoduo.jxc.service.CostAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostAllocationServiceImpl extends ServiceImpl<CostAllocationMapper, CostAllocation> implements CostAllocationService {

    @Override
    public PageResult<CostAllocationDTO> pageQuery(CostAllocationQuery query) {
        Page<CostAllocation> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CostAllocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getAllocationNo()), CostAllocation::getAllocationNo, query.getAllocationNo())
               .eq(StringUtils.hasText(query.getAccountPeriod()), CostAllocation::getAccountPeriod, query.getAccountPeriod())
               .eq(query.getCostType() != null, CostAllocation::getCostType, query.getCostType())
               .eq(query.getPoolId() != null, CostAllocation::getPoolId, query.getPoolId())
               .eq(query.getFromCenterId() != null, CostAllocation::getFromCenterId, query.getFromCenterId())
               .eq(query.getToCenterId() != null, CostAllocation::getToCenterId, query.getToCenterId())
               .eq(query.getStyleId() != null, CostAllocation::getStyleId, query.getStyleId())
               .eq(query.getStatus() != null, CostAllocation::getStatus, query.getStatus())
               .orderByDesc(CostAllocation::getCreateTime);
        page(page, wrapper);
        List<CostAllocationDTO> dtoList = page.getRecords().stream().map(this::toDTO).toList();
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CostAllocationDTO getDetail(Long allocationId) {
        CostAllocation entity = getById(allocationId);
        if (entity == null) return null;
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CostAllocationDTO dto) {
        CostAllocation entity = new CostAllocation();
        BeanUtils.copyProperties(dto, entity);
        entity.setAllocationNo(generateAllocationNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        save(entity);
        return entity.getAllocationId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CostAllocationDTO dto) {
        CostAllocation entity = new CostAllocation();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long allocationId) {
        removeById(allocationId);
    }

    private CostAllocationDTO toDTO(CostAllocation entity) {
        CostAllocationDTO dto = new CostAllocationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateAllocationNo() {
        return "CA" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
