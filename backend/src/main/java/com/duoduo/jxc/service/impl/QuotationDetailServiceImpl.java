package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.QuotationDetail;
import com.duoduo.jxc.mapper.QuotationDetailMapper;
import com.duoduo.jxc.service.QuotationDetailService;
import org.springframework.stereotype.Service;

@Service
public class QuotationDetailServiceImpl extends ServiceImpl<QuotationDetailMapper, QuotationDetail> implements QuotationDetailService {
}
