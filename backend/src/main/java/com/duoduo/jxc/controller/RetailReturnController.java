package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.sales.RetailReturnDTO;
import com.duoduo.jxc.service.RetailReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales/retail-return")
@RequiredArgsConstructor
public class RetailReturnController {

    private final RetailReturnService retailReturnService;

    @Log(title = "零售退货", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('sales:return:view')")
    public Result<PageResult<RetailReturnDTO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(retailReturnService.pageQuery(pageNum, pageSize, keyword, status, startDate, endDate));
    }

    @Log(title = "零售退货", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('sales:return:view')")
    public Result<RetailReturnDTO> getDetail(@PathVariable Long id) {
        return Result.success(retailReturnService.getDetail(id));
    }

    @Log(title = "零售退货", action = "新增退货单")
    @PostMapping
    @PreAuthorize("@perm.has('sales:return:add')")
    public Result<Long> create(@RequestBody RetailReturnDTO dto) {
        return Result.success(retailReturnService.create(dto));
    }

    @Log(title = "零售退货", action = "审核退货单")
    @PutMapping("/{id}/audit")
    @PreAuthorize("@perm.has('sales:return:audit')")
    public Result<Void> audit(@PathVariable Long id,
                              @RequestParam Long auditBy,
                              @RequestParam String auditByName) {
        retailReturnService.audit(id, auditBy, auditByName);
        return Result.success();
    }

    @Log(title = "零售退货", action = "删除退货单")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('sales:return:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        retailReturnService.delete(id);
        return Result.success();
    }
}
