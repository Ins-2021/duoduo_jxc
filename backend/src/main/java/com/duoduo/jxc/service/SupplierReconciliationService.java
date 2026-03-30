package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.SupplierReconciliationDTO;
import com.duoduo.jxc.dto.data.SupplierReconciliationQuery;
import com.duoduo.jxc.entity.SupplierReconciliation;

public interface SupplierReconciliationService extends IService<SupplierReconciliation> {

    PageResult<SupplierReconciliationDTO> pageQuery(SupplierReconciliationQuery query);

    SupplierReconciliationDTO getDetail(Long id);

    Long create(SupplierReconciliationDTO dto);

    void update(SupplierReconciliationDTO dto);

    void confirm(Long id);
}
