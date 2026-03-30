package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.fabric.FabricAlertDTO;
import com.duoduo.jxc.dto.fabric.FabricInventoryDTO;
import com.duoduo.jxc.dto.fabric.FabricInventoryQuery;
import com.duoduo.jxc.entity.FabricInventory;

import java.math.BigDecimal;
import java.util.List;

public interface FabricInventoryService extends IService<FabricInventory> {
    PageResult<FabricInventoryDTO> pageQuery(FabricInventoryQuery query);
    List<FabricInventoryDTO> getByFabricId(Long fabricId);
    void addStock(Long warehouseId, Long fabricId, BigDecimal quantity, String batchNo, BigDecimal price);
    void deductStock(Long warehouseId, Long fabricId, BigDecimal quantity);
    void lockStock(Long warehouseId, Long fabricId, BigDecimal quantity);
    void unlockStock(Long warehouseId, Long fabricId, BigDecimal quantity);
    List<FabricAlertDTO> checkAlerts();
}
