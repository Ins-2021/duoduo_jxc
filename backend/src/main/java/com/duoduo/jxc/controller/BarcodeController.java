package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.inventory.BarcodeDTO;
import com.duoduo.jxc.dto.inventory.BarcodeGenerateRequest;
import com.duoduo.jxc.dto.inventory.BarcodeRuleDTO;
import com.duoduo.jxc.service.BarcodeRuleService;
import com.duoduo.jxc.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory/barcode")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;
    private final BarcodeRuleService barcodeRuleService;

    // ---- 条码记录 ----

    @Log(title = "条码管理", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('inventory:barcode:view')")
    public Result<PageResult<BarcodeDTO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String barcodeType) {
        return Result.success(barcodeService.pageQuery(pageNum, pageSize, keyword, barcodeType));
    }

    @Log(title = "条码管理", action = "生成条码")
    @PostMapping("/generate")
    @PreAuthorize("@perm.has('inventory:barcode:add')")
    public Result<List<BarcodeDTO>> generate(@RequestBody BarcodeGenerateRequest request) {
        return Result.success(barcodeService.generateBarcode(request));
    }

    @Log(title = "条码管理", action = "解析条码")
    @GetMapping("/parse")
    @PreAuthorize("@perm.has('inventory:barcode:view')")
    public Result<BarcodeDTO> parse(@RequestParam String content) {
        return Result.success(barcodeService.parseBarcode(content));
    }

    @Log(title = "条码管理", action = "打印条码")
    @PostMapping("/print/{id}")
    @PreAuthorize("@perm.has('inventory:barcode:edit')")
    public Result<Void> print(@PathVariable Long id) {
        barcodeService.printBarcode(id);
        return Result.success();
    }

    @Log(title = "条码管理", action = "批量打印条码")
    @PostMapping("/print/batch")
    @PreAuthorize("@perm.has('inventory:barcode:edit')")
    public Result<Void> printBatch(@RequestBody List<Long> ids) {
        barcodeService.printBarcodeBatch(ids);
        return Result.success();
    }

    @Log(title = "条码管理", action = "删除条码")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('inventory:barcode:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        barcodeService.deleteBarcode(id);
        return Result.success();
    }

    // ---- 条码规则 ----

    @Log(title = "条码规则", action = "分页查询")
    @GetMapping("/rule/page")
    @PreAuthorize("@perm.has('inventory:barcode:view')")
    public Result<PageResult<BarcodeRuleDTO>> rulePage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String ruleType) {
        return Result.success(barcodeRuleService.pageQuery(pageNum, pageSize, keyword, ruleType));
    }

    @Log(title = "条码规则", action = "查看规则详情")
    @GetMapping("/rule/{id}")
    @PreAuthorize("@perm.has('inventory:barcode:view')")
    public Result<BarcodeRuleDTO> ruleDetail(@PathVariable Long id) {
        return Result.success(barcodeRuleService.getDetail(id));
    }

    @Log(title = "条码规则", action = "新增规则")
    @PostMapping("/rule")
    @PreAuthorize("@perm.has('inventory:barcode:add')")
    public Result<Long> ruleCreate(@RequestBody BarcodeRuleDTO dto) {
        return Result.success(barcodeRuleService.create(dto));
    }

    @Log(title = "条码规则", action = "修改规则")
    @PutMapping("/rule/{id}")
    @PreAuthorize("@perm.has('inventory:barcode:edit')")
    public Result<Void> ruleUpdate(@PathVariable Long id, @RequestBody BarcodeRuleDTO dto) {
        dto.setRuleId(id);
        barcodeRuleService.update(dto);
        return Result.success();
    }

    @Log(title = "条码规则", action = "删除规则")
    @DeleteMapping("/rule/{id}")
    @PreAuthorize("@perm.has('inventory:barcode:delete')")
    public Result<Void> ruleDelete(@PathVariable Long id) {
        barcodeRuleService.delete(id);
        return Result.success();
    }
}
