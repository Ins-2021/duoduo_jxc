package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.InventoryDTO;
import com.duoduo.jxc.dto.inventory.InventoryQuery;
import com.duoduo.jxc.entity.Inventory;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.enums.InventoryTransTypeEnum;
import com.duoduo.jxc.mapper.InventoryMapper;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private final InventoryConverter converter;
    private final InventoryTransactionService inventoryTransactionService;

    @Override
    public PageResult<InventoryDTO> pageQuery(InventoryQuery query) {
        Page<Inventory> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(query.getWarehouseId() != null, Inventory::getWarehouseId, query.getWarehouseId())
               .eq(query.getSkuId() != null, Inventory::getSkuId, query.getSkuId());

        page(page, wrapper);
        List<InventoryDTO> dtoList = converter.toDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStock(Long warehouseId, Long skuId, Integer qty, Long orderId) {
        // 使用悲观锁查询库存
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
               .eq(Inventory::getSkuId, skuId)
               .last("FOR UPDATE");

        Inventory inventory = getOne(wrapper);

        if (inventory == null) {
            throw new BusinessException(BizCode.INVENTORY_LOCK_FAILED);
        }

        // 校验可用库存
        int availableQty = inventory.getAvailableQty() != null ? inventory.getAvailableQty() : inventory.getQty();
        if (availableQty < qty) {
            throw new BusinessException(BizCode.INVENTORY_LOCK_FAILED);
        }

        // 更新库存：可用库存减少，锁定库存增加
        int newAvailableQty = availableQty - qty;
        int newLockedQty = (inventory.getLockedQty() != null ? inventory.getLockedQty() : 0) + qty;

        LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Inventory::getInventoryId, inventory.getInventoryId())
                     .set(Inventory::getAvailableQty, newAvailableQty)
                     .set(Inventory::getLockedQty, newLockedQty);

        update(updateWrapper);

        // 记录库存流水
        inventoryTransactionService.record(
                warehouseId, skuId,
                InventoryTransTypeEnum.LOCK.getCode(),
                qty,
                availableQty,
                newAvailableQty,
                "BOOKING_ORDER",
                orderId,
                null
        );

        log.info("库存锁定成功: warehouseId={}, skuId={}, qty={}, orderId={}", warehouseId, skuId, qty, orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockStock(Long warehouseId, Long skuId, Integer qty, Long orderId) {
        // 使用悲观锁查询库存
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
               .eq(Inventory::getSkuId, skuId)
               .last("FOR UPDATE");

        Inventory inventory = getOne(wrapper);

        if (inventory == null) {
            throw new BusinessException(BizCode.INVENTORY_UNLOCK_FAILED);
        }

        // 校验锁定库存
        int lockedQty = inventory.getLockedQty() != null ? inventory.getLockedQty() : 0;
        if (lockedQty < qty) {
            throw new BusinessException(BizCode.INVENTORY_UNLOCK_FAILED);
        }

        // 更新库存：可用库存增加，锁定库存减少
        int availableQty = inventory.getAvailableQty() != null ? inventory.getAvailableQty() : inventory.getQty();
        int newAvailableQty = availableQty + qty;
        int newLockedQty = lockedQty - qty;

        LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Inventory::getInventoryId, inventory.getInventoryId())
                     .set(Inventory::getAvailableQty, newAvailableQty)
                     .set(Inventory::getLockedQty, newLockedQty);

        update(updateWrapper);

        // 记录库存流水
        inventoryTransactionService.record(
                warehouseId, skuId,
                InventoryTransTypeEnum.UNLOCK.getCode(),
                qty,
                availableQty,
                newAvailableQty,
                "BOOKING_ORDER_UNLOCK",
                orderId,
                null
        );

        log.info("库存解锁成功: warehouseId={}, skuId={}, qty={}, orderId={}", warehouseId, skuId, qty, orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long warehouseId, Long skuId, Integer qty,
                            String billType, Long billId, String billNo) {
        // 使用悲观锁查询库存
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
               .eq(Inventory::getSkuId, skuId)
               .last("FOR UPDATE");

        Inventory inventory = getOne(wrapper);

        if (inventory == null) {
            throw new BusinessException(BizCode.STOCK_INSUFFICIENT);
        }

        // 校验可用库存
        int availableQty = inventory.getAvailableQty() != null ? inventory.getAvailableQty() : inventory.getQty();
        if (availableQty < qty) {
            throw new BusinessException(BizCode.INVENTORY_DEDUCT_FAILED);
        }

        // 校验总库存
        int totalQty = inventory.getQty() != null ? inventory.getQty() : 0;
        if (totalQty < qty) {
            throw new BusinessException(BizCode.STOCK_INSUFFICIENT);
        }

        // 更新库存：总库存和可用库存都减少
        int newTotalQty = totalQty - qty;
        int newAvailableQty = availableQty - qty;

        LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Inventory::getInventoryId, inventory.getInventoryId())
                     .set(Inventory::getQty, newTotalQty)
                     .set(Inventory::getAvailableQty, newAvailableQty);

        update(updateWrapper);

        log.info("库存扣减成功: warehouseId={}, skuId={}, qty={}, billType={}, billNo={}",
                warehouseId, skuId, qty, billType, billNo);

        // 记录库存流水
        inventoryTransactionService.record(
                warehouseId, skuId,
                InventoryTransTypeEnum.STOCK_OUT.getCode(),
                qty,
                availableQty,
                newAvailableQty,
                billType,
                billId,
                billNo
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addStock(Long warehouseId, Long skuId, Integer qty,
                         String billType, Long billId, String billNo) {
        // 使用悲观锁查询库存
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
               .eq(Inventory::getSkuId, skuId)
               .last("FOR UPDATE");

        Inventory inventory = getOne(wrapper);

        if (inventory == null) {
            // 如果库存记录不存在，创建新记录
            inventory = new Inventory();
            inventory.setWarehouseId(warehouseId);
            inventory.setSkuId(skuId);
            inventory.setQty(qty);
            inventory.setAvailableQty(qty);
            inventory.setLockedQty(0);
            save(inventory);
        } else {
            // 更新库存：总库存和可用库存都增加
            int totalQty = inventory.getQty() != null ? inventory.getQty() : 0;
            int availableQty = inventory.getAvailableQty() != null ? inventory.getAvailableQty() : totalQty;

            int newTotalQty = totalQty + qty;
            int newAvailableQty = availableQty + qty;

            LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Inventory::getInventoryId, inventory.getInventoryId())
                         .set(Inventory::getQty, newTotalQty)
                         .set(Inventory::getAvailableQty, newAvailableQty);

            update(updateWrapper);
        }

        log.info("库存增加成功: warehouseId={}, skuId={}, qty={}, billType={}, billNo={}",
                warehouseId, skuId, qty, billType, billNo);

        // 记录库存流水
        int beforeQty = inventory != null ? inventory.getQty() : 0;
        int afterQty = beforeQty + qty;
        inventoryTransactionService.record(
                warehouseId, skuId,
                InventoryTransTypeEnum.STOCK_IN.getCode(),
                qty,
                beforeQty,
                afterQty,
                billType,
                billId,
                billNo
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductFromLocked(Long warehouseId, Long skuId, Integer qty,
                                  String billType, Long billId, String billNo) {
        // 使用悲观锁查询库存
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId)
               .eq(Inventory::getSkuId, skuId)
               .last("FOR UPDATE");

        Inventory inventory = getOne(wrapper);

        if (inventory == null) {
            throw new BusinessException(BizCode.STOCK_INSUFFICIENT);
        }

        // 校验总库存
        int totalQty = inventory.getQty() != null ? inventory.getQty() : 0;
        if (totalQty < qty) {
            throw new BusinessException(BizCode.STOCK_INSUFFICIENT);
        }

        // 校验锁定库存
        int lockedQty = inventory.getLockedQty() != null ? inventory.getLockedQty() : 0;
        if (lockedQty < qty) {
            throw new BusinessException(BizCode.INVENTORY_UNLOCK_FAILED);
        }

        // 更新库存：总库存和锁定库存都减少，可用库存不变
        int newTotalQty = totalQty - qty;
        int newLockedQty = lockedQty - qty;
        int availableQty = inventory.getAvailableQty() != null ? inventory.getAvailableQty() : 0;

        LambdaUpdateWrapper<Inventory> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Inventory::getInventoryId, inventory.getInventoryId())
                     .set(Inventory::getQty, newTotalQty)
                     .set(Inventory::getLockedQty, newLockedQty);

        update(updateWrapper);

        log.info("从锁定库存中扣减: warehouseId={}, skuId={}, qty={}, billType={}, billNo={}",
                warehouseId, skuId, qty, billType, billNo);

        // 记录库存流水
        inventoryTransactionService.record(
                warehouseId, skuId,
                InventoryTransTypeEnum.STOCK_OUT.getCode(),
                qty,
                availableQty,
                availableQty,  // 可用库存不变
                billType,
                billId,
                billNo
        );
    }
}
