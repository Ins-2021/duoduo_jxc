package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PayrollSheetDTO;
import com.duoduo.jxc.dto.wage.PayrollSheetQuery;
import com.duoduo.jxc.entity.wage.PayrollSheet;

public interface PayrollSheetService extends IService<PayrollSheet> {
    PageResult<PayrollSheetDTO> pageQuery(PayrollSheetQuery query);
    PayrollSheetDTO getDetail(Long id);
    Long create(PayrollSheetDTO dto);
    void update(PayrollSheetDTO dto);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    void submit(Long id);
    void audit(Long id, Integer approved);
}
