package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.dto.product.ProductCategoryDTO;
import com.duoduo.jxc.dto.product.ProductCategoryTreeNode;
import com.duoduo.jxc.entity.ProductCategory;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.mapper.ProductCategoryMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final ProductSpuMapper productSpuMapper;

    @Override
    public List<ProductCategoryTreeNode> tree() {
        List<ProductCategory> list = list(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getDeleted, 0)
                .eq(ProductCategory::getStatus, 1)
                .orderByAsc(ProductCategory::getSort)
                .orderByAsc(ProductCategory::getCategoryId));

        if (list == null || list.isEmpty()) {
            return List.of();
        }

        Map<Long, List<ProductCategory>> byParent = list.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() == null ? 0L : c.getParentId()));

        Set<Long> allIds = list.stream().map(ProductCategory::getCategoryId).collect(Collectors.toSet());
        List<ProductCategory> roots = list.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0L || !allIds.contains(c.getParentId()))
                .sorted(Comparator.comparing((ProductCategory c) -> c.getSort() == null ? 0 : c.getSort())
                        .thenComparing(ProductCategory::getCategoryId))
                .toList();

        List<ProductCategoryTreeNode> nodes = new ArrayList<>();
        for (ProductCategory root : roots) {
            nodes.add(toNode(root, byParent));
        }
        return nodes;
    }

    private ProductCategoryTreeNode toNode(ProductCategory c, Map<Long, List<ProductCategory>> byParent) {
        ProductCategoryTreeNode n = new ProductCategoryTreeNode();
        n.setId(c.getCategoryId());
        n.setParentId(c.getParentId() == null ? 0L : c.getParentId());
        n.setLabel(c.getCategoryName());
        n.setSort(c.getSort() == null ? 0 : c.getSort());
        List<ProductCategory> children = byParent.getOrDefault(c.getCategoryId(), List.of());
        if (children.isEmpty()) {
            n.setChildren(List.of());
            return n;
        }
        List<ProductCategoryTreeNode> childNodes = children.stream()
                .sorted(Comparator.comparing((ProductCategory x) -> x.getSort() == null ? 0 : x.getSort())
                        .thenComparing(ProductCategory::getCategoryId))
                .map(x -> toNode(x, byParent))
                .toList();
        n.setChildren(childNodes);
        return n;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCategory(ProductCategoryDTO dto) {
        checkCategoryNameUnique(dto.getName(), dto.getParentId(), null);
        
        ProductCategory category = new ProductCategory();
        category.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        category.setCategoryName(dto.getName());
        category.setSort(dto.getSort() == null ? 0 : dto.getSort());
        category.setStatus(1); // 默认启用
        
        save(category);
        return category.getCategoryId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(ProductCategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "分类ID不能为空");
        }
        if (dto.getId().equals(dto.getParentId())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "上级分类不能是自己");
        }
        
        checkCategoryNameUnique(dto.getName(), dto.getParentId(), dto.getId());
        
        ProductCategory category = getById(dto.getId());
        if (category == null) {
            throw new BusinessException(BizCode.CATEGORY_NOT_FOUND);
        }
        
        category.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        category.setCategoryName(dto.getName());
        category.setSort(dto.getSort() == null ? 0 : dto.getSort());
        
        updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        // 检查是否有子分类
        long count = count(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getParentId, id)
                .eq(ProductCategory::getDeleted, 0));
        if (count > 0) {
            throw new BusinessException(BizCode.CATEGORY_HAS_CHILDREN);
        }

        // 检查是否有关联的商品
        long productCount = productSpuMapper.selectCount(new LambdaQueryWrapper<ProductSpu>()
                .eq(ProductSpu::getCategoryId, id)
                .eq(ProductSpu::getDeleted, 0));
        if (productCount > 0) {
            throw new BusinessException(BizCode.CATEGORY_HAS_PRODUCTS);
        }

        removeById(id);
    }

    private void checkCategoryNameUnique(String name, Long parentId, Long excludeId) {
        Long pId = parentId == null ? 0L : parentId;
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getCategoryName, name)
                .eq(ProductCategory::getParentId, pId)
                .eq(ProductCategory::getDeleted, 0);
        
        if (excludeId != null) {
            wrapper.ne(ProductCategory::getCategoryId, excludeId);
        }
        
        if (count(wrapper) > 0) {
            throw new BusinessException(BizCode.CATEGORY_NAME_DUPLICATE);
        }
    }
}

