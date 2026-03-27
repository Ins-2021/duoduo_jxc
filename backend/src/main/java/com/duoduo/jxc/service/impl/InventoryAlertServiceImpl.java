package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.InventoryAlertDTO;
import com.duoduo.jxc.entity.InventoryAlert;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.InventoryAlertMapper;
import com.duoduo.jxc.service.InventoryAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存预警Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryAlertServiceImpl extends ServiceImpl<InventoryAlertMapper, InventoryAlert> implements InventoryAlertService {

    private final InventoryConverter converter;

    @Override
    public PageResult<InventoryAlertDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<InventoryAlert> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(InventoryAlert::getSkuCode, keyword)
                    .or().like(InventoryAlert::getSkuName, keyword)
                    .or().like(InventoryAlert::getWarehouseName, keyword));
        }
        Integer alertType = (Integer) query.getParams().get("alertType");
        if (alertType != null) {
            wrapper.eq(InventoryAlert::getAlertType, alertType);
        }
        Integer status = (Integer) query.getParams().get("status");
        if (status != null) {
            wrapper.eq(InventoryAlert::getStatus, status);
        }
        wrapper.orderByDesc(InventoryAlert::getAlertTime);

        IPage<InventoryAlert> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<InventoryAlertDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(InventoryAlertDTO dto) {
        InventoryAlert entity = new InventoryAlert();
        BeanUtils.copyProperties(dto, entity);
        entity.setStatus(0);
        entity.setAlertTime(LocalDateTime.now());
        save(entity);
        return entity.getAlertId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(InventoryAlertDTO dto) {
        InventoryAlert entity = new InventoryAlert();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsHandled(Long id) {
        InventoryAlert alert = super.getById(id);
        if (alert == null) {
            throw new BusinessException("预警记录不存在");
        }
        alert.setStatus(1);
        updateById(alert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAlerts() {
        log.info("执行库存预警检查...");
    }

    private InventoryAlertDTO toDTO(InventoryAlert entity) {
        InventoryAlertDTO dto = new InventoryAlertDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
