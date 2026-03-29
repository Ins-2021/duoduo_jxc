package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.entity.QuotationDetail;

import java.util.List;

public interface QuotationDetailService extends IService<QuotationDetail> {
    
    List<QuotationDetail> getByQuotationId(Long quotationId);
}
