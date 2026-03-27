package com.duoduo.jxc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.entity.rbac.SysPost;
import com.duoduo.jxc.service.rbac.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/post")
public class SystemPostController {

    @Autowired
    private SysPostService postService;

    @GetMapping("/page")
    @PreAuthorize("@perm.has('system:post:view')")
    public Result<Page<SysPost>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String postCode,
            @RequestParam(required = false) String postName,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getDeleted, 0);
        if (StringUtils.hasText(postCode)) {
            wrapper.like(SysPost::getPostCode, postCode);
        }
        if (StringUtils.hasText(postName)) {
            wrapper.like(SysPost::getPostName, postName);
        }
        if (status != null) {
            wrapper.eq(SysPost::getStatus, status);
        }
        wrapper.orderByAsc(SysPost::getPostSort);

        Page<SysPost> page = postService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    @GetMapping("/list")
    @PreAuthorize("@perm.has('system:post:view')")
    public Result<List<SysPost>> list() {
        return Result.success(postService.lambdaQuery().eq(SysPost::getDeleted, 0).eq(SysPost::getStatus, 1).list());
    }

    @GetMapping("/{postId}")
    @PreAuthorize("@perm.has('system:post:view')")
    public Result<SysPost> getInfo(@PathVariable("postId") Long postId) {
        return Result.success(postService.getById(postId));
    }

    @PostMapping
    @PreAuthorize("@perm.has('system:post:add')")
    public Result<?> add(@RequestBody SysPost post) {
        postService.save(post);
        return Result.success();
    }

    @PutMapping
    @PreAuthorize("@perm.has('system:post:edit')")
    public Result<?> edit(@RequestBody SysPost post) {
        postService.updateById(post);
        return Result.success();
    }

    @DeleteMapping("/{postIds}")
    @PreAuthorize("@perm.has('system:post:delete')")
    public Result<?> remove(@PathVariable("postIds") List<Long> postIds) {
        postService.removeByIds(postIds);
        return Result.success();
    }
}