package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.fabric.FabricDTO;
import com.duoduo.jxc.dto.fabric.FabricQuery;
import com.duoduo.jxc.entity.Fabric;

import java.util.List;

public interface FabricService extends IService<Fabric> {
    PageResult<FabricDTO> pageQuery(FabricQuery query);
    FabricDTO getDetail(Long id);
    Long create(FabricDTO dto);
    void update(FabricDTO dto);
    void delete(Long id);
    List<FabricDTO> listAll();
}
