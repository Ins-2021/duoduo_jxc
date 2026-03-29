package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostCenterDTO;
import com.duoduo.jxc.dto.cost.CostCenterQuery;
import com.duoduo.jxc.entity.cost.CostCenter;

public interface CostCenterService extends IService<CostCenter> {

    PageResult<CostCenterDTO> pageQuery(CostCenterQuery query);

    CostCenterDTO getDetail(Long costCenterId);

    Long create(CostCenterDTO dto);

    void update(CostCenterDTO dto);

    void delete(Long costCenterId);
}
