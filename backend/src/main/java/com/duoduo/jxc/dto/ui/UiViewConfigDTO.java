package com.duoduo.jxc.dto.ui;

import lombok.Data;

@Data
public class UiViewConfigDTO {

    private String viewKey;

    private String viewName;

    private String scene;

    private Integer version;

    private Object columns;
}

