package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.entity.rbac.SysDept;
import com.duoduo.jxc.service.rbac.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/dept")
public class SystemDeptController {

    @Autowired
    private SysDeptService deptService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('system:dept:view')")
    public Result<List<SysDept>> list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.success(deptService.buildDeptTree(depts));
    }

    @GetMapping("/list/exclude/{deptId}")
    @PreAuthorize("@perm.has('system:dept:view')")
    public Result<List<SysDept>> excludeChild(@PathVariable("deptId") Long deptId) {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        // remove self and children
        depts.removeIf(d -> d.getDeptId().equals(deptId) || 
                (d.getAncestors() != null && d.getAncestors().contains("," + deptId + ",")));
        return Result.success(deptService.buildDeptTree(depts));
    }

    @GetMapping("/{deptId}")
    @PreAuthorize("@perm.has('system:dept:view')")
    public Result<SysDept> getInfo(@PathVariable("deptId") Long deptId) {
        return Result.success(deptService.getById(deptId));
    }

    @PostMapping
    @PreAuthorize("@perm.has('system:dept:add')")
    public Result<?> add(@RequestBody SysDept dept) {
        // update ancestors
        if (dept.getParentId() != null && dept.getParentId() > 0) {
            SysDept parent = deptService.getById(dept.getParentId());
            dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
        } else {
            dept.setAncestors("0");
        }
        deptService.save(dept);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("@perm.has('system:dept:edit')")
    public Result<?> edit(@RequestBody SysDept dept) {
        if (dept.getParentId() != null && dept.getParentId() > 0) {
            SysDept parent = deptService.getById(dept.getParentId());
            dept.setAncestors(parent.getAncestors() + "," + dept.getParentId());
        } else {
            dept.setAncestors("0");
        }
        deptService.updateById(dept);
        return Result.success();
    }

    @DeleteMapping("/{deptId}")
    @PreAuthorize("@perm.has('system:dept:delete')")
    public Result<?> remove(@PathVariable("deptId") Long deptId) {
        if (deptService.lambdaQuery().eq(SysDept::getParentId, deptId).count() > 0) {
            return Result.error("存在下级部门,不允许删除");
        }
        deptService.removeById(deptId);
        return Result.success();
    }
}