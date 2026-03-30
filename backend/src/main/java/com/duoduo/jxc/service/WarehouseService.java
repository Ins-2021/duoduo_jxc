package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.WarehouseDTO;
import com.duoduo.jxc.dto.data.WarehouseQuery;
import com.duoduo.jxc.entity.Warehouse;

public interface WarehouseService extends IService<Warehouse> {

    PageResult<Warehouse> pageQuery(WarehouseQuery query);

    Warehouse getDetail(Long id);

    Long create(WarehouseDTO dto);

    void update(WarehouseDTO dto);

    void updateStatus(Long id, Integer status);
}
