package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FabricConverter;
import com.duoduo.jxc.dto.fabric.FabricAlertDTO;
import com.duoduo.jxc.dto.fabric.FabricInventoryDTO;
import com.duoduo.jxc.dto.fabric.FabricInventoryQuery;
import com.duoduo.jxc.entity.Fabric;
import com.duoduo.jxc.entity.FabricInventory;
import com.duoduo.jxc.entity.Warehouse;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.FabricInventoryMapper;
import com.duoduo.jxc.mapper.FabricMapper;
import com.duoduo.jxc.mapper.WarehouseMapper;
import com.duoduo.jxc.service.FabricInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FabricInventoryServiceImpl extends ServiceImpl<FabricInventoryMapper, FabricInventory> implements FabricInventoryService {

    private final FabricConverter converter;
    private final FabricMapper fabricMapper;
    private final WarehouseMapper warehouseMapper;

    @Override
    public PageResult<FabricInventoryDTO> pageQuery(FabricInventoryQuery query) {
        Page<FabricInventory> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FabricInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getFabricId() != null, FabricInventory::getFabricId, query.getFabricId())
               .eq(query.getWarehouseId() != null, FabricInventory::getWarehouseId, query.getWarehouseId());
        page(page, wrapper);
        List<FabricInventoryDTO> dtoList = enrichDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public List<FabricInventoryDTO> getByFabricId(Long fabricId) {
        LambdaQueryWrapper<FabricInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FabricInventory::getFabricId, fabricId);
        return enrichDTOList(list(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStock(Long warehouseId, Long fabricId, BigDecimal quantity, String batchNo, BigDecimal price) {
        LambdaQueryWrapper<FabricInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FabricInventory::getWarehouseId, warehouseId)
               .eq(FabricInventory::getFabricId, fabricId)
               .eq(StringUtils.hasText(batchNo), FabricInventory::getBatchNo, batchNo)
               .last("FOR UPDATE");
        FabricInventory inventory = getOne(wrapper);
        if (inventory == null) {
            inventory = new FabricInventory();
            inventory.setWarehouseId(warehouseId);
            inventory.setFabricId(fabricId);
            inventory.setQuantity(quantity);
            inventory.setAvailableQty(quantity);
            inventory.setLockedQty(BigDecimal.ZERO);
            inventory.setBatchNo(batchNo);
            inventory.setCostPrice(price);
            inventory.setUpdateTime(LocalDateTime.now());
            save(inventory);
        } else {
            BigDecimal newQty = inventory.getQuantity().add(quantity);
            BigDecimal newAvailableQty = inventory.getAvailableQty().add(quantity);
            LambdaUpdateWrapper<FabricInventory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(FabricInventory::getInventoryId, inventory.getInventoryId())
                        .set(FabricInventory::getQuantity, newQty)
                        .set(FabricInventory::getAvailableQty, newAvailableQty)
                        .set(FabricInventory::getCostPrice, price)
                        .set(FabricInventory::getUpdateTime, LocalDateTime.now());
            update(updateWrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long warehouseId, Long fabricId, BigDecimal quantity) {
        LambdaQueryWrapper<FabricInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FabricInventory::getWarehouseId, warehouseId)
               .eq(FabricInventory::getFabricId, fabricId)
               .last("FOR UPDATE");
        FabricInventory inventory = getOne(wrapper);
        if (inventory == null || inventory.getAvailableQty().compareTo(quantity) < 0) {
            throw new BusinessException(BizCode.STOCK_INSUFFICIENT, "面料库存不足");
        }
        BigDecimal newQty = inventory.getQuantity().subtract(quantity);
        BigDecimal newAvailableQty = inventory.getAvailableQty().subtract(quantity);
        LambdaUpdateWrapper<FabricInventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FabricInventory::getInventoryId, inventory.getInventoryId())
                    .set(FabricInventory::getQuantity, newQty)
                    .set(FabricInventory::getAvailableQty, newAvailableQty)
                    .set(FabricInventory::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long warehouseId, Long fabricId, BigDecimal quantity) {
        LambdaQueryWrapper<FabricInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FabricInventory::getWarehouseId, warehouseId)
               .eq(FabricInventory::getFabricId, fabricId)
               .last("FOR UPDATE");
        FabricInventory inventory = getOne(wrapper);
        if (inventory == null || inventory.getAvailableQty().compareTo(quantity) < 0) {
            throw new BusinessException(BizCode.INVENTORY_LOCK_FAILED, "面料库存锁定失败");
        }
        BigDecimal newAvailableQty = inventory.getAvailableQty().subtract(quantity);
        BigDecimal newLockedQty = inventory.getLockedQty().add(quantity);
        LambdaUpdateWrapper<FabricInventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FabricInventory::getInventoryId, inventory.getInventoryId())
                    .set(FabricInventory::getAvailableQty, newAvailableQty)
                    .set(FabricInventory::getLockedQty, newLockedQty)
                    .set(FabricInventory::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockStock(Long warehouseId, Long fabricId, BigDecimal quantity) {
        LambdaQueryWrapper<FabricInventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FabricInventory::getWarehouseId, warehouseId)
               .eq(FabricInventory::getFabricId, fabricId)
               .last("FOR UPDATE");
        FabricInventory inventory = getOne(wrapper);
        if (inventory == null || inventory.getLockedQty().compareTo(quantity) < 0) {
            throw new BusinessException(BizCode.INVENTORY_UNLOCK_FAILED, "面料库存解锁失败");
        }
        BigDecimal newAvailableQty = inventory.getAvailableQty().add(quantity);
        BigDecimal newLockedQty = inventory.getLockedQty().subtract(quantity);
        LambdaUpdateWrapper<FabricInventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FabricInventory::getInventoryId, inventory.getInventoryId())
                    .set(FabricInventory::getAvailableQty, newAvailableQty)
                    .set(FabricInventory::getLockedQty, newLockedQty)
                    .set(FabricInventory::getUpdateTime, LocalDateTime.now());
        update(updateWrapper);
    }

    @Override
    public List<FabricAlertDTO> checkAlerts() {
        List<FabricAlertDTO> alerts = new ArrayList<>();
        List<FabricInventory> inventories = list();
        for (FabricInventory inv : inventories) {
            Fabric fabric = fabricMapper.selectById(inv.getFabricId());
            if (fabric == null) continue;
            if (fabric.getMinStock() != null && inv.getQuantity().compareTo(fabric.getMinStock()) < 0) {
                FabricAlertDTO alert = new FabricAlertDTO();
                alert.setFabricId(inv.getFabricId());
                alert.setFabricCode(fabric.getFabricCode());
                alert.setFabricName(fabric.getFabricName());
                alert.setAlertType("low_stock");
                alert.setMessage("面料库存不足");
                alert.setCurrentQty(inv.getQuantity());
                alert.setThresholdQty(fabric.getMinStock());
                alerts.add(alert);
            }
            if (fabric.getMaxStock() != null && inv.getQuantity().compareTo(fabric.getMaxStock()) > 0) {
                FabricAlertDTO alert = new FabricAlertDTO();
                alert.setFabricId(inv.getFabricId());
                alert.setFabricCode(fabric.getFabricCode());
                alert.setFabricName(fabric.getFabricName());
                alert.setAlertType("over_stock");
                alert.setMessage("面料库存积压");
                alert.setCurrentQty(inv.getQuantity());
                alert.setThresholdQty(fabric.getMaxStock());
                alerts.add(alert);
            }
        }
        return alerts;
    }

    private List<FabricInventoryDTO> enrichDTOList(List<FabricInventory> list) {
        List<FabricInventoryDTO> dtoList = new ArrayList<>();
        for (FabricInventory inv : list) {
            FabricInventoryDTO dto = converter.toInventoryDTO(inv);
            Fabric fabric = fabricMapper.selectById(inv.getFabricId());
            if (fabric != null) {
                dto.setFabricCode(fabric.getFabricCode());
                dto.setFabricName(fabric.getFabricName());
            }
            Warehouse warehouse = warehouseMapper.selectById(inv.getWarehouseId());
            if (warehouse != null) {
                dto.setWarehouseName(warehouse.getWarehouseName());
            }
            dtoList.add(dto);
        }
        return dtoList;
    }
}
