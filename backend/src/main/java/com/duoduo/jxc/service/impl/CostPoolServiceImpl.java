package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostPoolDTO;
import com.duoduo.jxc.dto.cost.CostPoolQuery;
import com.duoduo.jxc.entity.cost.CostPool;
import com.duoduo.jxc.mapper.CostPoolMapper;
import com.duoduo.jxc.service.CostPoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostPoolServiceImpl extends ServiceImpl<CostPoolMapper, CostPool> implements CostPoolService {

    @Override
    public PageResult<CostPoolDTO> pageQuery(CostPoolQuery query) {
        Page<CostPool> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CostPool> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPoolNo()), CostPool::getPoolNo, query.getPoolNo())
               .eq(StringUtils.hasText(query.getAccountPeriod()), CostPool::getAccountPeriod, query.getAccountPeriod())
               .eq(query.getCostType() != null, CostPool::getCostType, query.getCostType())
               .eq(query.getCostCenterId() != null, CostPool::getCostCenterId, query.getCostCenterId())
               .eq(query.getStyleId() != null, CostPool::getStyleId, query.getStyleId())
               .eq(query.getStatus() != null, CostPool::getStatus, query.getStatus())
               .orderByDesc(CostPool::getCreateTime);
        page(page, wrapper);
        List<CostPoolDTO> dtoList = page.getRecords().stream().map(this::toDTO).toList();
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CostPoolDTO getDetail(Long poolId) {
        CostPool entity = getById(poolId);
        if (entity == null) return null;
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CostPoolDTO dto) {
        CostPool entity = new CostPool();
        BeanUtils.copyProperties(dto, entity);
        entity.setPoolNo(generatePoolNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getAllocatedAmount() == null) {
            entity.setAllocatedAmount(BigDecimal.ZERO);
        }
        if (entity.getRemainingAmount() == null) {
            entity.setRemainingAmount(entity.getTotalAmount() != null ? entity.getTotalAmount() : BigDecimal.ZERO);
        }
        save(entity);
        return entity.getPoolId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CostPoolDTO dto) {
        CostPool entity = new CostPool();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long poolId) {
        removeById(poolId);
    }

    private CostPoolDTO toDTO(CostPool entity) {
        CostPoolDTO dto = new CostPoolDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generatePoolNo() {
        return "CP" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
