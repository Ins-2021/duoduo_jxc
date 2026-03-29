package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.production.CutOrderDTO;
import com.duoduo.jxc.dto.production.CutOrderQuery;
import com.duoduo.jxc.entity.CutOrder;

public interface CutOrderService extends IService<CutOrder> {
    PageResult<CutOrderDTO> pageQuery(CutOrderQuery query);
    CutOrderDTO getDetail(Long id);
    Long create(CutOrderDTO dto);
    void update(CutOrderDTO dto);
    void delete(Long id);
    void updateStatus(Long id, String status);
    void completeCutting(Long id, Integer cutQuantity);
}
