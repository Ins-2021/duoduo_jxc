package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.RetailReturnDTO;
import com.duoduo.jxc.entity.RetailReturn;

public interface RetailReturnService extends IService<RetailReturn> {

    PageResult<RetailReturnDTO> pageQuery(int pageNum, int pageSize, String keyword, Integer status, String startDate, String endDate);

    Long create(RetailReturnDTO dto);

    void audit(Long returnId, Long auditBy, String auditByName);

    void delete(Long returnId);

    RetailReturnDTO getDetail(Long returnId);
}
