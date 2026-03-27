package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.InventoryTransaction;
import com.duoduo.jxc.mapper.InventoryTransactionMapper;
import com.duoduo.jxc.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryTransactionServiceImpl
        extends ServiceImpl<InventoryTransactionMapper, InventoryTransaction>
        implements InventoryTransactionService {

    @Override
    public void record(Long warehouseId, Long skuId, Integer transType, Integer qty,
                       Integer beforeQty, Integer afterQty,
                       String billType, Long billId, String billNo) {
        InventoryTransaction trans = new InventoryTransaction();
        trans.setWarehouseId(warehouseId);
        trans.setSkuId(skuId);
        trans.setTransType(transType);
        trans.setQty(qty);
        trans.setBeforeQty(beforeQty);
        trans.setAfterQty(afterQty);
        trans.setBillType(billType);
        trans.setBillId(billId);
        trans.setBillNo(billNo);
        save(trans);

        log.debug("库存流水记录: warehouseId={}, skuId={}, transType={}, qty={}",
                warehouseId, skuId, transType, qty);
    }
}
