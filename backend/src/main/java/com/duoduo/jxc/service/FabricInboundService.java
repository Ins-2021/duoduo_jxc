package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.fabric.FabricInboundDTO;
import com.duoduo.jxc.dto.fabric.FabricInboundQuery;
import com.duoduo.jxc.entity.FabricInbound;

public interface FabricInboundService extends IService<FabricInbound> {
    PageResult<FabricInboundDTO> pageQuery(FabricInboundQuery query);
    FabricInboundDTO getDetail(Long id);
    Long create(FabricInboundDTO dto);
    void approve(Long id);
    void delete(Long id);
}
