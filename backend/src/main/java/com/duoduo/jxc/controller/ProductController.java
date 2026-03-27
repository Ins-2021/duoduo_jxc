package com.duoduo.jxc.controller;

import com.duoduo.jxc.annotation.Log;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.product.ProductCategoryDTO;
import com.duoduo.jxc.dto.product.ProductCategoryTreeNode;
import com.duoduo.jxc.dto.product.ProductSpuDTO;
import com.duoduo.jxc.dto.product.ProductSpuQuery;
import com.duoduo.jxc.dto.product.ProductSkuSelectDTO;
import com.duoduo.jxc.dto.product.ProductSkuSelectQuery;
import com.duoduo.jxc.service.ProductCategoryService;
import com.duoduo.jxc.service.ProductSelectService;
import com.duoduo.jxc.service.ProductSpuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductSpuService productSpuService;
    private final ProductCategoryService productCategoryService;
    private final ProductSelectService productSelectService;

    @Log(title = "商品管理", action = "分页查询商品")
    @GetMapping("/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:product:view')")
    public Result<PageResult<ProductSpuDTO>> page(ProductSpuQuery query) {
        return Result.success(productSpuService.pageQuery(query));
    }

    @Log(title = "商品管理", action = "查询分类树")
    @GetMapping("/categories/tree")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:product:view')")
    public Result<List<ProductCategoryTreeNode>> categoryTree() {
        return Result.success(productCategoryService.tree());
    }

    @Log(title = "商品分类", action = "新增分类")
    @PostMapping("/categories")
    @PreAuthorize("@perm.has('data:product:add')")
    public Result<Long> addCategory(@RequestBody @Validated ProductCategoryDTO dto) {
        return Result.success(productCategoryService.addCategory(dto));
    }

    @Log(title = "商品分类", action = "修改分类")
    @PutMapping("/categories")
    @PreAuthorize("@perm.has('data:product:edit')")
    public Result<Void> updateCategory(@RequestBody @Validated ProductCategoryDTO dto) {
        productCategoryService.updateCategory(dto);
        return Result.success();
    }

    @Log(title = "商品分类", action = "删除分类")
    @DeleteMapping("/categories/{id}")
    @PreAuthorize("@perm.has('data:product:delete')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        productCategoryService.deleteCategory(id);
        return Result.success();
    }

    @Log(title = "商品管理", action = "分页查询SKU(开单选品)")
    @GetMapping("/sku/page")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:product:view')")
    public Result<PageResult<ProductSkuSelectDTO>> skuPage(ProductSkuSelectQuery query) {
        return Result.success(productSelectService.pageSku(query));
    }

    @Log(title = "商品管理", action = "查询商品详情")
    @GetMapping("/{id}")
    @PreAuthorize("@perm.has('data:menu:view') or @perm.has('data:product:view')")
    public Result<ProductSpuDTO> getById(@PathVariable Long id) {
        return Result.success(productSpuService.getDetail(id));
    }

    @Log(title = "商品管理", action = "新增商品")
    @PostMapping
    @PreAuthorize("@perm.has('data:product:add')")
    public Result<Long> add(@RequestBody @Validated ProductSpuDTO dto) {
        return Result.success(productSpuService.saveProduct(dto));
    }

    @Log(title = "商品管理", action = "修改商品")
    @PutMapping
    @PreAuthorize("@perm.has('data:product:edit')")
    public Result<Void> update(@RequestBody @Validated ProductSpuDTO dto) {
        productSpuService.updateProduct(dto);
        return Result.success();
    }

    @Log(title = "商品管理", action = "删除商品")
    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:product:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        productSpuService.deleteProduct(id);
        return Result.success();
    }
}
