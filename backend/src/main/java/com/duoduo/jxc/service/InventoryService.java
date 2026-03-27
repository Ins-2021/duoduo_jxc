package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.InventoryDTO;
import com.duoduo.jxc.dto.inventory.InventoryQuery;
import com.duoduo.jxc.entity.Inventory;

public interface InventoryService extends IService<Inventory> {
    PageResult<InventoryDTO> pageQuery(InventoryQuery query);
}
