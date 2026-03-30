package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 生产订单状态枚举
 */
@Getter
public enum ProductionOrderStatusEnum implements DictEnum {
    DRAFT(0, "草稿"),
    PENDING(1, "待审核"),
    APPROVED(2, "已审核"),
    IN_PROGRESS(3, "生产中"),
    COMPLETED(4, "已完成"),
    CLOSED(5, "已关闭"),
    VOIDED(90, "已作废");

    private final int code;
    private final String label;

    ProductionOrderStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
