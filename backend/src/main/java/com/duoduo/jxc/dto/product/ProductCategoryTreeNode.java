package com.duoduo.jxc.dto.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductCategoryTreeNode {
    private Long id;
    private Long parentId;
    private String label;
    private Integer sort;
    private List<ProductCategoryTreeNode> children;
}

