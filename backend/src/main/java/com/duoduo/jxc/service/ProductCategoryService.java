package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.product.ProductCategoryDTO;
import com.duoduo.jxc.dto.product.ProductCategoryTreeNode;
import com.duoduo.jxc.entity.ProductCategory;

import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {
    List<ProductCategoryTreeNode> tree();
    Long addCategory(ProductCategoryDTO dto);
    void updateCategory(ProductCategoryDTO dto);
    void deleteCategory(Long id);
}

