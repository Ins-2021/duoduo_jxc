package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.QuotationDetail;
import com.duoduo.jxc.mapper.QuotationDetailMapper;
import com.duoduo.jxc.service.QuotationDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuotationDetailServiceImpl extends ServiceImpl<QuotationDetailMapper, QuotationDetail> implements QuotationDetailService {

    @Override
    public List<QuotationDetail> getByQuotationId(Long quotationId) {
        LambdaQueryWrapper<QuotationDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuotationDetail::getQuotationId, quotationId)
               .orderByAsc(QuotationDetail::getSortOrder);
        return this.list(wrapper);
    }
}
