package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.inventory.AssemblyOrderDTO;
import com.duoduo.jxc.entity.AssemblyOrder;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

public interface AssemblyOrderService extends IService<AssemblyOrder> {
    PageResult<AssemblyOrderDTO> pageList(PageQuery query);
    AssemblyOrderDTO getById(Long id);
    Long create(AssemblyOrderDTO dto);
    void update(AssemblyOrderDTO dto);
    void delete(Long id);
    void approve(Long id);
}
