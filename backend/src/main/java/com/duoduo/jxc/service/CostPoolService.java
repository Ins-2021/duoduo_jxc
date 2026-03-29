package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.CostPoolDTO;
import com.duoduo.jxc.dto.cost.CostPoolQuery;
import com.duoduo.jxc.entity.cost.CostPool;

public interface CostPoolService extends IService<CostPool> {

    PageResult<CostPoolDTO> pageQuery(CostPoolQuery query);

    CostPoolDTO getDetail(Long poolId);

    Long create(CostPoolDTO dto);

    void update(CostPoolDTO dto);

    void delete(Long poolId);
}
