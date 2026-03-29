package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.QuotationDTO;
import com.duoduo.jxc.dto.sales.QuotationQuery;
import com.duoduo.jxc.entity.Quotation;

public interface QuotationService extends IService<Quotation> {
    PageResult<QuotationDTO> pageQuery(QuotationQuery query);
    QuotationDTO getDetail(Long id);
    Long create(QuotationDTO dto);
    void update(QuotationDTO dto);
    void delete(Long id);
    Long convertToSalesOrder(Long quotationId);
}
