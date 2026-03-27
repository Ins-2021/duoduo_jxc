package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.inventory.InventoryAlertDTO;
import com.duoduo.jxc.entity.InventoryAlert;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

public interface InventoryAlertService extends IService<InventoryAlert> {
    PageResult<InventoryAlertDTO> pageList(PageQuery query);
    void markAsHandled(Long id);
    void checkAlerts();
}
