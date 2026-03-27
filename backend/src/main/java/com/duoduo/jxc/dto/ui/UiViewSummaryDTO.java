package com.duoduo.jxc.dto.ui;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UiViewSummaryDTO {

    private String viewKey;

    private String viewName;

    private String scene;

    private Integer version;

    private LocalDateTime updateTime;
}

