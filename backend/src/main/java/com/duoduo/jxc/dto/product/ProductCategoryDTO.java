package com.duoduo.jxc.dto.product;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductCategoryDTO {

    private Long id;

    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "排序不能为空")
    private Integer sort;
}
