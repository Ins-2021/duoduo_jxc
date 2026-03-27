package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.inventory.StockInOutDTO;
import com.duoduo.jxc.entity.StockInOut;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

public interface StockInOutService extends IService<StockInOut> {
    PageResult<StockInOutDTO> pageList(PageQuery query);
    StockInOutDTO getById(Long id);
    Long create(StockInOutDTO dto);
    void update(StockInOutDTO dto);
    void delete(Long id);
    void approve(Long id);
}
