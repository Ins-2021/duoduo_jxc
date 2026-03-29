package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostAllocationDTO;
import com.duoduo.jxc.dto.cost.CostAllocationQuery;
import com.duoduo.jxc.entity.cost.CostAllocation;

public interface CostAllocationService extends IService<CostAllocation> {

    PageResult<CostAllocationDTO> pageQuery(CostAllocationQuery query);

    CostAllocationDTO getDetail(Long allocationId);

    Long create(CostAllocationDTO dto);

    void update(CostAllocationDTO dto);

    void delete(Long allocationId);
}
