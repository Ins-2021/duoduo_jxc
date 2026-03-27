package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.InventoryDTO;
import com.duoduo.jxc.dto.inventory.InventoryQuery;
import com.duoduo.jxc.entity.Inventory;

public interface InventoryService extends IService<Inventory> {
    PageResult<InventoryDTO> pageQuery(InventoryQuery query);

    /**
     * 锁定库存（预订单审核时调用）
     *
     * @param warehouseId 仓库ID
     * @param skuId SKU ID
     * @param qty 锁定数量
     * @param orderId 关联订单ID
     */
    void lockStock(Long warehouseId, Long skuId, Integer qty, Long orderId);

    /**
     * 解锁库存（预订单取消时调用）
     *
     * @param warehouseId 仓库ID
     * @param skuId SKU ID
     * @param qty 解锁数量
     * @param orderId 关联订单ID
     */
    void unlockStock(Long warehouseId, Long skuId, Integer qty, Long orderId);

    /**
     * 扣减库存（销售单发货时调用）
     *
     * @param warehouseId 仓库ID
     * @param skuId SKU ID
     * @param qty 扣减数量
     * @param billType 源单据类型
     * @param billId 源单据ID
     * @param billNo 源单据编号
     */
    void deductStock(Long warehouseId, Long skuId, Integer qty,
                     String billType, Long billId, String billNo);

    /**
     * 增加库存（入库时调用）
     *
     * @param warehouseId 仓库ID
     * @param skuId        SKU ID
     * @param qty          增加数量
     * @param billType     源单据类型
     * @param billId       源单据ID
     * @param billNo       源单据编号
     */
    void addStock(Long warehouseId, Long skuId, Integer qty,
                  String billType, Long billId, String billNo);

    /**
     * 从锁定库存中扣减（预订单发货时调用）
     * 预订单已锁定库存，发货时只需扣减 lockedQty 和总库存
     *
     * @param warehouseId 仓库ID
     * @param skuId        SKU ID
     * @param qty          扣减数量
     * @param billType     源单据类型
     * @param billId       源单据ID
     * @param billNo       源单据编号
     */
    void deductFromLocked(Long warehouseId, Long skuId, Integer qty,
                          String billType, Long billId, String billNo);
}
