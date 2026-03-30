package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.entity.QuotationDetail;
import com.duoduo.jxc.service.QuotationDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotationdetail")
@RequiredArgsConstructor
public class QuotationDetailController {

    private final QuotationDetailService quotationDetailService;

    @Log(title = "报价明细", action = "根据报价ID查询")
    @GetMapping("/quotation/{quotationId}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:quotation:view')")
    public Result<List<QuotationDetail>> getByQuotationId(@PathVariable Long quotationId) {
        return Result.success(quotationDetailService.getByQuotationId(quotationId));
    }

    @Log(title = "报价明细", action = "查询详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:quotation:view')")
    public Result<QuotationDetail> getById(@PathVariable Long id) {
        return Result.success(quotationDetailService.getById(id));
    }

    @Log(title = "报价明细", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:quotation:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        quotationDetailService.removeById(id);
        return Result.success();
    }

    @Log(title = "报价明细", action = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("@perm.has('data:quotation:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        quotationDetailService.removeByIds(ids);
        return Result.success();
    }
}
