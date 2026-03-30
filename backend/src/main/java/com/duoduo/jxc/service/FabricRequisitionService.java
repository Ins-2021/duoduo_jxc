package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.fabric.FabricRequisitionDTO;
import com.duoduo.jxc.dto.fabric.FabricRequisitionQuery;
import com.duoduo.jxc.entity.FabricRequisition;

public interface FabricRequisitionService extends IService<FabricRequisition> {
    PageResult<FabricRequisitionDTO> pageQuery(FabricRequisitionQuery query);
    FabricRequisitionDTO getDetail(Long id);
    Long create(FabricRequisitionDTO dto);
    void approve(Long id, String comment, boolean approved);
    void issue(Long id);
    void delete(Long id);
}
