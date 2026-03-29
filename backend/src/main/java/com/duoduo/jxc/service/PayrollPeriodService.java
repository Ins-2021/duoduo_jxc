package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PayrollPeriodDTO;
import com.duoduo.jxc.dto.wage.PayrollPeriodQuery;
import com.duoduo.jxc.entity.PayrollPeriod;

public interface PayrollPeriodService extends IService<PayrollPeriod> {
    PageResult<PayrollPeriodDTO> pageQuery(PayrollPeriodQuery query);
    PayrollPeriodDTO getDetail(Long id);
    Long create(PayrollPeriodDTO dto);
    void update(PayrollPeriodDTO dto);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    void submit(Long id);
    void audit(Long id, boolean approved);
}
