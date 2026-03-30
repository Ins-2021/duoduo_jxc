package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 工序大类枚举
 */
@Getter
public enum ProcessCategoryEnum {
    CUTTING("cutting", "裁剪"),
    SEWING("sewing", "缝纫"),
    FINISHING("finishing", "后整"),
    PACKING("packing", "包装"),
    IRONING("ironing", "熨烫"),
    INSPECTION("inspection", "检验"),
    OUTSOURCE("outsource", "外协");

    private final String code;
    private final String label;

    ProcessCategoryEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
