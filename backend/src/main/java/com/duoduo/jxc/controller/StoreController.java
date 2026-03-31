package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.store.StoreDTO;
import com.duoduo.jxc.dto.store.StoreQuery;
import com.duoduo.jxc.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店管理控制器
 *
 * @author duoduo
 * @since 2026-03-31
 */
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Log(title = "门店管理", action = "分页查询门店")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:store:view')")
    public Result<PageResult<StoreDTO>> page(StoreQuery query) {
        return Result.success(storeService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:store:view')")
    public Result<StoreDTO> getById(@PathVariable Long id) {
        return Result.success(storeService.getDetail(id));
    }

    @GetMapping("/list")
    @PreAuthorize("@perm.has('data:store:view')")
    public Result<List<StoreDTO>> list() {
        return Result.success(storeService.listEnabled());
    }

    @Log(title = "门店管理", action = "新增门店")
    @PostMapping
    @PreAuthorize("@perm.has('data:store:add')")
    public Result<Long> create(@RequestBody @Validated StoreDTO dto) {
        return Result.success(storeService.createStore(dto));
    }

    @Log(title = "门店管理", action = "修改门店")
    @PutMapping
    @PreAuthorize("@perm.has('data:store:edit')")
    public Result<Void> update(@RequestBody @Validated StoreDTO dto) {
        storeService.updateStore(dto);
        return Result.success();
    }

    @Log(title = "门店管理", action = "删除门店")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:store:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        storeService.deleteStore(id);
        return Result.success();
    }
}
