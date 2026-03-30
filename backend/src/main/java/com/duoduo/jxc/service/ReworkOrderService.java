package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ReworkOrderDTO;
import com.duoduo.jxc.dto.ReworkOrderQuery;
import com.duoduo.jxc.entity.ReworkOrder;

public interface ReworkOrderService extends IService<ReworkOrder> {
    PageResult<ReworkOrderDTO> pageQuery(ReworkOrderQuery query);
    ReworkOrderDTO getDetail(Long id);
    Long create(ReworkOrderDTO dto);
    void update(ReworkOrderDTO dto);
    void delete(Long id);
    ReworkOrderDTO getByCheckId(Long checkId);
}
