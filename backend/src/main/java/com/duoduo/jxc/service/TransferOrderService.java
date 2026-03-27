package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.inventory.TransferOrderDTO;
import com.duoduo.jxc.entity.TransferOrder;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

public interface TransferOrderService extends IService<TransferOrder> {
    PageResult<TransferOrderDTO> pageList(PageQuery query);
    TransferOrderDTO getById(Long id);
    Long create(TransferOrderDTO dto);
    void update(TransferOrderDTO dto);
    void delete(Long id);
    void approve(Long id);
    void reject(Long id);
}
