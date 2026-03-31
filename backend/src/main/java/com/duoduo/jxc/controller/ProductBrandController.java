package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.ProductBrandDTO;
import com.duoduo.jxc.entity.ProductBrand;
import com.duoduo.jxc.service.ProductBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品品牌Controller
 */
@RestController
@RequestMapping("/product/brand")
@RequiredArgsConstructor
public class ProductBrandController {

    private final ProductBrandService productBrandService;

    @Log(title = "商品品牌", action = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:brand:view')")
    public Result<PageResult<ProductBrandDTO>> pageQuery(PageQuery query) {
        return Result.success(productBrandService.pageQuery(query));
    }

    @Log(title = "商品品牌", action = "查看详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:brand:view')")
    public Result<ProductBrandDTO> getDetail(@PathVariable Long id) {
        return Result.success(productBrandService.getDetail(id));
    }

    @Log(title = "商品品牌", action = "新增")
    @PostMapping
    @PreAuthorize("@perm.has('data:brand:add')")
    public Result<Long> create(@RequestBody ProductBrandDTO dto) {
        return Result.success(productBrandService.create(dto));
    }

    @Log(title = "商品品牌", action = "修改")
    @PutMapping
    @PreAuthorize("@perm.has('data:brand:edit')")
    public Result<Void> update(@RequestBody ProductBrandDTO dto) {
        productBrandService.update(dto);
        return Result.success();
    }

    @Log(title = "商品品牌", action = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:brand:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        productBrandService.delete(id);
        return Result.success();
    }

    /**
     * 品牌下拉列表（不分页）
     */
    @GetMapping("/list")
    @PreAuthorize("@perm.has('data:brand:view')")
    public Result<List<ProductBrand>> listAll() {
        return Result.success(productBrandService.list());
    }
}
