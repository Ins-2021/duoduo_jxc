package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.ProductCostDTO;
import com.duoduo.jxc.dto.cost.ProductCostQuery;
import com.duoduo.jxc.entity.cost.ProductCost;
import com.duoduo.jxc.mapper.ProductCostMapper;
import com.duoduo.jxc.service.ProductCostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCostServiceImpl extends ServiceImpl<ProductCostMapper, ProductCost> implements ProductCostService {

    @Override
    public PageResult<ProductCostDTO> pageQuery(ProductCostQuery query) {
        Page<ProductCost> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProductCost> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getCostNo()), ProductCost::getCostNo, query.getCostNo())
               .eq(StringUtils.hasText(query.getAccountPeriod()), ProductCost::getAccountPeriod, query.getAccountPeriod())
               .eq(query.getStyleId() != null, ProductCost::getStyleId, query.getStyleId())
               .like(StringUtils.hasText(query.getStyleCode()), ProductCost::getStyleCode, query.getStyleCode())
               .eq(query.getProductionId() != null, ProductCost::getProductionId, query.getProductionId())
               .eq(query.getStatus() != null, ProductCost::getStatus, query.getStatus())
               .orderByDesc(ProductCost::getCreateTime);
        page(page, wrapper);
        List<ProductCostDTO> dtoList = page.getRecords().stream().map(this::toDTO).toList();
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProductCostDTO getDetail(Long productCostId) {
        ProductCost entity = getById(productCostId);
        if (entity == null) return null;
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductCostDTO dto) {
        ProductCost entity = new ProductCost();
        BeanUtils.copyProperties(dto, entity);
        entity.setCostNo(generateCostNo());
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        if (entity.getMaterialCost() == null) entity.setMaterialCost(BigDecimal.ZERO);
        if (entity.getLaborCost() == null) entity.setLaborCost(BigDecimal.ZERO);
        if (entity.getOverheadCost() == null) entity.setOverheadCost(BigDecimal.ZERO);
        if (entity.getTotalCost() == null) {
            entity.setTotalCost(entity.getMaterialCost().add(entity.getLaborCost()).add(entity.getOverheadCost()));
        }
        if (entity.getOutputQuantity() != null && entity.getOutputQuantity().compareTo(BigDecimal.ZERO) > 0) {
            entity.setUnitCost(entity.getTotalCost().divide(entity.getOutputQuantity(), 4, RoundingMode.HALF_UP));
        }
        save(entity);
        return entity.getProductCostId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductCostDTO dto) {
        ProductCost entity = new ProductCost();
        BeanUtils.copyProperties(dto, entity);
        if (entity.getMaterialCost() == null) entity.setMaterialCost(BigDecimal.ZERO);
        if (entity.getLaborCost() == null) entity.setLaborCost(BigDecimal.ZERO);
        if (entity.getOverheadCost() == null) entity.setOverheadCost(BigDecimal.ZERO);
        if (entity.getTotalCost() == null) {
            entity.setTotalCost(entity.getMaterialCost().add(entity.getLaborCost()).add(entity.getOverheadCost()));
        }
        if (entity.getOutputQuantity() != null && entity.getOutputQuantity().compareTo(BigDecimal.ZERO) > 0 && entity.getTotalCost() != null) {
            entity.setUnitCost(entity.getTotalCost().divide(entity.getOutputQuantity(), 4, RoundingMode.HALF_UP));
        }
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long productCostId) {
        removeById(productCostId);
    }

    private ProductCostDTO toDTO(ProductCost entity) {
        ProductCostDTO dto = new ProductCostDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateCostNo() {
        return "PC" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
