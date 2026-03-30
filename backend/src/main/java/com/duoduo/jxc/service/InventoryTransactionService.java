package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.InventoryTransactionDTO;
import com.duoduo.jxc.dto.inventory.InventoryTransactionQuery;
import com.duoduo.jxc.entity.InventoryTransaction;

public interface InventoryTransactionService extends IService<InventoryTransaction> {

    /**
     * 记录库存流水
     *
     * @param warehouseId 仓库ID
     * @param skuId        SKU ID
     * @param transType    流水类型
     * @param qty          变动数量
     * @param beforeQty    变动前库存
     * @param afterQty     变动后库存
     * @param billType     源单据类型
     * @param billId       源单据ID
     * @param billNo       源单据编号
     */
    void record(Long warehouseId, Long skuId, Integer transType, Integer qty,
                Integer beforeQty, Integer afterQty,
                String billType, Long billId, String billNo);

    PageResult<InventoryTransactionDTO> pageQuery(InventoryTransactionQuery query);

    InventoryTransactionDTO getDetail(Long id);
}
