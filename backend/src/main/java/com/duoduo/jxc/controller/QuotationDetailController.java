package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.QuotationDetail;
import com.duoduo.jxc.service.QuotationDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotationdetail")
@RequiredArgsConstructor
public class QuotationDetailController {

    private final QuotationDetailService quotationDetailService;

}
