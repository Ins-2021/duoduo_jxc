package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.QuotationDTO;
import com.duoduo.jxc.dto.sales.QuotationQuery;
import com.duoduo.jxc.entity.Quotation;
import com.duoduo.jxc.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotation")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('sales:quotation:view')")
    public Result<PageResult<QuotationDTO>> pageQuery(QuotationQuery query) {
        return Result.success(quotationService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('sales:quotation:view')")
    public Result<QuotationDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(quotationService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("@perm.has('sales:quotation:add')")
    public Result<Long> create(@RequestBody QuotationDTO dto) {
        return Result.success(quotationService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('sales:quotation:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody QuotationDTO dto) {
        dto.setQuotationId(id);
        quotationService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('sales:quotation:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        quotationService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/convert")
    @PreAuthorize("@perm.has('sales:quotation:convert')")
    public Result<Long> convertToSalesOrder(@PathVariable("id") Long id) {
        Long orderId = quotationService.convertToSalesOrder(id);
        return Result.success(orderId);
    }
}
