package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostCenterDTO;
import com.duoduo.jxc.dto.cost.CostCenterQuery;
import com.duoduo.jxc.entity.cost.CostCenter;
import com.duoduo.jxc.mapper.CostCenterMapper;
import com.duoduo.jxc.service.CostCenterService;
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
public class CostCenterServiceImpl extends ServiceImpl<CostCenterMapper, CostCenter> implements CostCenterService {

    @Override
    public PageResult<CostCenterDTO> pageQuery(CostCenterQuery query) {
        Page<CostCenter> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CostCenter> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getCenterCode()), CostCenter::getCenterCode, query.getCenterCode())
               .like(StringUtils.hasText(query.getCenterName()), CostCenter::getCenterName, query.getCenterName())
               .eq(query.getCenterType() != null, CostCenter::getCenterType, query.getCenterType())
               .eq(query.getStatus() != null, CostCenter::getStatus, query.getStatus())
               .orderByDesc(CostCenter::getCreateTime);
        page(page, wrapper);
        List<CostCenterDTO> dtoList = page.getRecords().stream().map(this::toDTO).toList();
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public CostCenterDTO getDetail(Long costCenterId) {
        CostCenter entity = getById(costCenterId);
        if (entity == null) return null;
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CostCenterDTO dto) {
        CostCenter entity = new CostCenter();
        BeanUtils.copyProperties(dto, entity);
        entity.setCenterCode(generateCenterCode());
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        save(entity);
        return entity.getCostCenterId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CostCenterDTO dto) {
        CostCenter entity = new CostCenter();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long costCenterId) {
        removeById(costCenterId);
    }

    private CostCenterDTO toDTO(CostCenter entity) {
        CostCenterDTO dto = new CostCenterDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateCenterCode() {
        return "CC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
