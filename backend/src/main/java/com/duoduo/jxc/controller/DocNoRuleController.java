package com.duoduo.jxc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.converter.DocNoRuleConverter;
import com.duoduo.jxc.dto.system.DocNoRuleDTO;
import com.duoduo.jxc.entity.DocNoRule;
import com.duoduo.jxc.service.DocNoRuleService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 单号规则Controller
 *
 * @author duoduo
 * @date 2026-03-25
 */
@RestController
@RequestMapping("/settings/doc-no-rule")
@RequiredArgsConstructor
public class DocNoRuleController {

    private final DocNoRuleService docNoRuleService;
    private final DocNoRuleConverter converter;

    /**
     * 分页查询
     */
    @PostMapping("/page")
    @PreAuthorize("@perm.has('settings:doc-no-rule:view')")
    public Result<PageResult<DocNoRuleDTO>> page(@RequestBody PageQuery query) {
        LambdaQueryWrapper<DocNoRule> wrapper = new LambdaQueryWrapper<>();

        // 单据名称模糊查询
        Object docName = query.getParam("docName");
        if (docName != null && StringUtils.hasText(docName.toString())) {
            wrapper.like(DocNoRule::getDocName, docName.toString().trim());
        }

        // 状态精确查询
        Object status = query.getParam("status");
        if (status != null && !status.toString().isEmpty()) {
            wrapper.eq(DocNoRule::getStatus, Integer.valueOf(status.toString()));
        }

        wrapper.orderByAsc(DocNoRule::getDocType);

        Page<DocNoRule> page = docNoRuleService.page(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        PageResult<DocNoRuleDTO> result = new PageResult<>(
            page.getTotal(),
            page.getRecords().stream().map(converter::toDTO).toList()
        );
        return Result.success(result);
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('settings:doc-no-rule:view')")
    public Result<DocNoRuleDTO> getById(@PathVariable Long id) {
        return Result.success(converter.toDTO(docNoRuleService.getById(id)));
    }

    /**
     * 新增
     */
    @PostMapping
    @Log(title = "单号规则", action = "新增")
    @PreAuthorize("@perm.has('settings:doc-no-rule:add')")
    public Result<Long> create(@RequestBody DocNoRuleDTO dto) {
        DocNoRule entity = converter.toEntity(dto);
        docNoRuleService.save(entity);
        return Result.success(entity.getRuleId());
    }

    /**
     * 更新
     */
    @PutMapping
    @Log(title = "单号规则", action = "修改")
    @PreAuthorize("@perm.has('settings:doc-no-rule:edit')")
    public Result<Void> update(@RequestBody DocNoRuleDTO dto) {
        DocNoRule entity = converter.toEntity(dto);
        docNoRuleService.updateById(entity);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @Log(title = "单号规则", action = "删除")
    @PreAuthorize("@perm.has('settings:doc-no-rule:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        docNoRuleService.removeById(id);
        return Result.success();
    }

    /**
     * 切换状态
     */
    @PutMapping("/{id}/toggle")
    @Log(title = "单号规则", action = "修改状态")
    @PreAuthorize("@perm.has('settings:doc-no-rule:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        DocNoRule entity = docNoRuleService.getById(id);
        entity.setStatus(entity.getStatus() == 1 ? 0 : 1);
        docNoRuleService.updateById(entity);
        return Result.success();
    }

    /**
     * 批量切换状态
     */
    @PutMapping("/batch-toggle")
    @Log(title = "单号规则", action = "批量修改状态")
    @PreAuthorize("@perm.has('settings:doc-no-rule:edit')")
    public Result<Void> batchToggleStatus(@RequestBody BatchToggleRequest request) {
        docNoRuleService.lambdaUpdate()
                .in(DocNoRule::getRuleId, request.getIds())
                .set(DocNoRule::getStatus, request.getStatus())
                .update();
        return Result.success();
    }

    /**
     * 批量切换状态请求体
     */
    @Data
    public static class BatchToggleRequest {
        private List<Long> ids;
        private Integer status;
    }
}
