package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.cost.StandardCostDTO;
import com.duoduo.jxc.dto.cost.StandardCostQuery;
import com.duoduo.jxc.entity.StandardCost;

public interface StandardCostService extends IService<StandardCost> {
    PageResult<StandardCostDTO> pageQuery(StandardCostQuery query);
    StandardCostDTO getDetail(Long id);
    Long create(StandardCostDTO dto);
    void update(StandardCostDTO dto);
    void delete(Long id);
}
