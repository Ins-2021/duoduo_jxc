package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.wage.PayrollSheetDetailDTO;
import com.duoduo.jxc.dto.wage.PayrollSheetDetailQuery;
import com.duoduo.jxc.entity.PayrollSheetDetail;

public interface PayrollSheetDetailService extends IService<PayrollSheetDetail> {
    PageResult<PayrollSheetDetailDTO> pageQuery(PayrollSheetDetailQuery query);
    PayrollSheetDetailDTO getDetail(Long id);
    Long create(PayrollSheetDetailDTO dto);
    void update(PayrollSheetDetailDTO dto);
    void delete(Long id);
}
