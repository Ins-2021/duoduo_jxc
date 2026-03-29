package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.QuotationDTO;
import com.duoduo.jxc.dto.sales.QuotationQuery;
import com.duoduo.jxc.entity.Quotation;
import com.duoduo.jxc.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotation")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @Log(title = "销售报价", action = "分页查询")
    @GetMapping("/list")
    @PreAuthorize("@perm.has('sales:quotation:view')")
    public Result<PageResult<QuotationDTO>> pageQuery(@Valid QuotationQuery query) {
        return Result.success(quotationService.pageQuery(query));
    }

    @Log(title = "销售报价", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('sales:quotation:view')")
    public Result<QuotationDTO> getDetail(@PathVariable("id") Long id) {
        return Result.success(quotationService.getDetail(id));
    }

    @Log(title = "销售报价", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('sales:quotation:add')")
    public Result<Long> create(@Valid @RequestBody QuotationDTO dto) {
        return Result.success(quotationService.create(dto));
    }

    @Log(title = "销售报价", action = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("@perm.has('sales:quotation:edit')")
    public Result<Void> update(@PathVariable("id") Long id, @Valid @RequestBody QuotationDTO dto) {
        dto.setQuotationId(id);
        quotationService.update(dto);
        return Result.success();
    }

    @Log(title = "销售报价", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('sales:quotation:delete')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        quotationService.delete(id);
        return Result.success();
    }

    @Log(title = "销售报价", action = "转销售订单")
    @PostMapping("/{id}/convert")
    @PreAuthorize("@perm.has('sales:quotation:convert')")
    public Result<Long> convertToSalesOrder(@PathVariable("id") Long id) {
        Long orderId = quotationService.convertToSalesOrder(id);
        return Result.success(orderId);
    }
}
