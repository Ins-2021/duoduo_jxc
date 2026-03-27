package com.duoduo.jxc.dto.rbac;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuSaveRequest {

    @NotNull
    private Long parentId;

    @NotBlank
    private String menuName;

    private Integer orderNum;

    private String path;

    private String component;

    private String routeName;

    private String icon;

    @NotBlank
    private String menuType;

    private Integer visible;

    private Integer status;

    private String perms;
}

