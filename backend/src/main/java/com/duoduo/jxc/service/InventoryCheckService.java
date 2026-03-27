package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.inventory.InventoryCheckDTO;
import com.duoduo.jxc.entity.InventoryCheck;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

public interface InventoryCheckService extends IService<InventoryCheck> {
    PageResult<InventoryCheckDTO> pageList(PageQuery query);
    InventoryCheckDTO getById(Long id);
    Long create(InventoryCheckDTO dto);
    void update(InventoryCheckDTO dto);
    void delete(Long id);
    void complete(Long id);
}
