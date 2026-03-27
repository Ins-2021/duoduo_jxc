package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.print.PrintDataDTO;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateDTO;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateEnabledDTO;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateQuery;
import com.duoduo.jxc.dto.printtemplate.PrintTemplateSettingsDTO;
import com.duoduo.jxc.service.PrintDataService;
import com.duoduo.jxc.service.PrintTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/print-template")
@RequiredArgsConstructor
public class PrintTemplateController {

    private final PrintTemplateService printTemplateService;
    private final PrintDataService printDataService;

    @Log(title = "打印模板", action = "分页查询模板")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('settings:print-template:view')")
    public Result<PageResult<PrintTemplateDTO>> page(PrintTemplateQuery query) {
        return Result.success(printTemplateService.pageQuery(query));
    }

    @Log(title = "打印模板", action = "查询业务类型列表")
    @GetMapping("/biz-types")
    @PreAuthorize("@perm.has('settings:print-template:view')")
    public Result<List<String>> bizTypes() {
        return Result.success(printTemplateService.listBizTypes());
    }

    @Log(title = "打印模板", action = "获取预览数据")
    @GetMapping("/{id}/preview-data")
    @PreAuthorize("@perm.has('settings:print-template:view')")
    public Result<PrintDataDTO> previewData(@PathVariable Long id, @RequestParam(required = false) String bizId) {
        return Result.success(printDataService.previewData(id, bizId));
    }

    @Log(title = "打印模板", action = "获取模板详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('settings:print-template:view')")
    public Result<PrintTemplateDTO> detail(@PathVariable Long id) {
        return Result.success(printTemplateService.getDetail(id));
    }

    @Log(title = "打印模板", action = "保存模板")
    @PostMapping
    @PreAuthorize("@perm.has('settings:print-template:edit')")
    public Result<PrintTemplateDTO> save(@RequestBody PrintTemplateDTO dto) {
        return Result.success(printTemplateService.saveTemplate(dto));
    }

    @Log(title = "打印模板", action = "删除模板")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('settings:print-template:edit')")
    public Result<Void> delete(@PathVariable Long id) {
        printTemplateService.removeById(id);
        return Result.success();
    }

    @Log(title = "打印模板", action = "复制模板")
    @PostMapping("/{id}/copy")
    @PreAuthorize("@perm.has('settings:print-template:edit')")
    public Result<Long> copy(@PathVariable Long id) {
        return Result.success(printTemplateService.copyTemplate(id));
    }

    @Log(title = "打印模板", action = "设为默认模板")
    @PostMapping("/{id}/set-default")
    @PreAuthorize("@perm.has('settings:print-template:edit')")
    public Result<Void> setDefault(@PathVariable Long id) {
        printTemplateService.setDefault(id);
        return Result.success();
    }

    @Log(title = "打印模板", action = "启用/停用模板")
    @PostMapping("/{id}/enabled")
    @PreAuthorize("@perm.has('settings:print-template:edit')")
    public Result<Void> enabled(@PathVariable Long id, @RequestBody PrintTemplateEnabledDTO dto) {
        printTemplateService.setEnabled(id, dto == null ? null : dto.getEnabled());
        return Result.success();
    }

    @Log(title = "打印模板", action = "获取打印设置")
    @GetMapping("/settings")
    @PreAuthorize("@perm.has('settings:print-template:view')")
    public Result<PrintTemplateSettingsDTO> settings() {
        return Result.success(printTemplateService.getSettings());
    }

    @Log(title = "打印模板", action = "保存打印设置")
    @PutMapping("/settings")
    @PreAuthorize("@perm.has('settings:print-template:edit')")
    public Result<Void> saveSettings(@RequestBody PrintTemplateSettingsDTO dto) {
        printTemplateService.saveSettings(dto);
        return Result.success();
    }
}
