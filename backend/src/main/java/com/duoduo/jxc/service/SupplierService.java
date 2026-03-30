package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.SupplierDTO;
import com.duoduo.jxc.dto.data.SupplierQuery;
import com.duoduo.jxc.entity.Supplier;

public interface SupplierService extends IService<Supplier> {

    PageResult<Supplier> pageQuery(SupplierQuery query);

    Supplier getDetail(Long id);

    Long create(SupplierDTO dto);

    void update(SupplierDTO dto);

    void updateStatus(Long id, String status);
}
