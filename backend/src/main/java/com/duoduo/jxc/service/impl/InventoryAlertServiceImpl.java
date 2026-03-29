package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.InventoryAlertDTO;
import com.duoduo.jxc.entity.InventoryAlert;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.InventoryAlertMapper;
import com.duoduo.jxc.entity.Inventory;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.mapper.InventoryMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.service.InventoryAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final InventoryMapper inventoryMapper;
    private final ProductSkuMapper productSkuMapper;

    @Override
    public PageResult<InventoryAlertDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<InventoryAlert> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(InventoryAlert::getSkuCode, keyword)
                    .or().like(InventoryAlert::getSkuName, keyword)
                    .or().like(InventoryAlert::getWarehouseName, keyword));
        }
        Object alertTypeObj = query.getParams().get("alertType");
        if (alertTypeObj != null && !alertTypeObj.toString().trim().isEmpty()) {
            Integer alertType = alertTypeObj instanceof Integer ? (Integer) alertTypeObj : Integer.valueOf(alertTypeObj.toString());
            wrapper.eq(InventoryAlert::getAlertType, alertType);
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
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
            throw new BusinessException(BizCode.NOT_FOUND, "预警记录不存在");
        }
        alert.setStatus(1);
        updateById(alert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAlerts() {
        log.info("执行库存预警检查...");

        // 1. 查询所有设置了预警阈值的SKU
        List<ProductSku> skuList = productSkuMapper.selectList(
                new LambdaQueryWrapper<ProductSku>()
                        .gt(ProductSku::getWarningQty, 0)
                        .eq(ProductSku::getStatus, 1));
        if (skuList == null || skuList.isEmpty()) {
            log.info("没有设置预警阈值的SKU，跳过");
            return;
        }

        Map<Long, ProductSku> skuMap = skuList.stream()
                .collect(Collectors.toMap(ProductSku::getSkuId, s -> s));

        // 2. 查询所有库存
        List<Inventory> inventoryList = inventoryMapper.selectList(null);
        if (inventoryList == null || inventoryList.isEmpty()) {
            log.info("库存为空，跳过");
            return;
        }

        // 3. 查询已有的未处理预警，避免重复
        List<InventoryAlert> existingAlerts = this.list(
                new LambdaQueryWrapper<InventoryAlert>()
                        .eq(InventoryAlert::getStatus, 0));
        Map<String, InventoryAlert> existingKeyMap = existingAlerts.stream()
                .collect(Collectors.toMap(
                        a -> a.getWarehouseId() + "_" + a.getSkuId(),
                        a -> a));

        // 4. 检查每个库存是否需要预警
        List<InventoryAlert> newAlerts = new ArrayList<>();
        for (Inventory inv : inventoryList) {
            ProductSku sku = skuMap.get(inv.getSkuId());
            if (sku == null) {
                continue;
            }
            int currentQty = inv.getQty() != null ? inv.getQty() : 0;
            String key = inv.getWarehouseId() + "_" + inv.getSkuId();

            // 库存不足预警
            if (currentQty < sku.getWarningQty() && !existingKeyMap.containsKey(key + "_LOW")) {
                InventoryAlert alert = new InventoryAlert();
                alert.setWarehouseId(inv.getWarehouseId());
                alert.setSkuId(inv.getSkuId());
                alert.setSkuCode(sku.getSkuCode());
                alert.setAttr1(sku.getAttr1());
                alert.setAttr2(sku.getAttr2());
                alert.setCurrentQty(currentQty);
                alert.setMinQty(sku.getWarningQty());
                alert.setAlertType(1); // 1-库存不足
                alert.setStatus(0);
                alert.setAlertTime(LocalDateTime.now());
                newAlerts.add(alert);
            }
            // 库存为零预警
            if (currentQty <= 0 && !existingKeyMap.containsKey(key + "_ZERO")) {
                InventoryAlert alert = new InventoryAlert();
                alert.setWarehouseId(inv.getWarehouseId());
                alert.setSkuId(inv.getSkuId());
                alert.setSkuCode(sku.getSkuCode());
                alert.setAttr1(sku.getAttr1());
                alert.setAttr2(sku.getAttr2());
                alert.setCurrentQty(currentQty);
                alert.setMinQty(sku.getWarningQty());
                alert.setAlertType(2); // 2-库存为零
                alert.setStatus(0);
                alert.setAlertTime(LocalDateTime.now());
                newAlerts.add(alert);
            }
        }

        // 5. 批量保存新预警
        if (!newAlerts.isEmpty()) {
            this.saveBatch(newAlerts);
            log.info("库存预警检查完成，新增预警{}条", newAlerts.size());
        } else {
            log.info("库存预警检查完成，无新增预警");
        }
    }

    private InventoryAlertDTO toDTO(InventoryAlert entity) {
        InventoryAlertDTO dto = new InventoryAlertDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
