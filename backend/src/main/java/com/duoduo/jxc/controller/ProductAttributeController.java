package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.product.ProductAttributeDTO;
import com.duoduo.jxc.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product/attribute")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService attributeService;

    @GetMapping("/list")
    @PreAuthorize("@perm.has('data:attribute:view')")
    public Result<List<ProductAttributeDTO>> list() {
        return Result.success(attributeService.listWithValues());
    }

    @PostMapping
    @PreAuthorize("@perm.has('data:attribute:add')")
    public Result<Void> addAttribute(@RequestBody ProductAttributeDTO dto) {
        attributeService.saveAttribute(dto);
        return Result.success(null);
    }

    @PutMapping
    @PreAuthorize("@perm.has('data:attribute:edit')")
    public Result<Void> updateAttribute(@RequestBody ProductAttributeDTO dto) {
        attributeService.updateAttribute(dto);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@perm.has('data:attribute:delete')")
    public Result<Void> deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return Result.success(null);
    }

    @PostMapping("/{attributeId}/value")
    @PreAuthorize("@perm.has('data:attribute:add')")
    public Result<Void> addAttributeValue(@PathVariable Long attributeId, @RequestBody Map<String, String> body) {
        attributeService.saveAttributeValue(attributeId, body.get("value"));
        return Result.success(null);
    }

    @PutMapping("/value/{id}")
    @PreAuthorize("@perm.has('data:attribute:edit')")
    public Result<Void> updateAttributeValue(@PathVariable Long id, @RequestBody Map<String, String> body) {
        attributeService.updateAttributeValue(id, body.get("value"));
        return Result.success(null);
    }

    @DeleteMapping("/value/{id}")
    @PreAuthorize("@perm.has('data:attribute:delete')")
    public Result<Void> deleteAttributeValue(@PathVariable Long id) {
        attributeService.deleteAttributeValue(id);
        return Result.success(null);
    }

    @PostMapping("/{attributeId}/values/batch")
    @PreAuthorize("@perm.has('data:attribute:edit')")
    public Result<Void> batchSaveAttributeValues(@PathVariable Long attributeId, @RequestBody Map<String, List<String>> body) {
        attributeService.batchSaveAttributeValues(attributeId, body.get("values"));
        return Result.success(null);
    }
}
