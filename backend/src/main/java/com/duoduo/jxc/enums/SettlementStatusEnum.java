package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 结算状态枚举
 */
@Getter
public enum SettlementStatusEnum {
    UNSETTLED("unsettled", "未结算"),
    PARTIAL("partial", "部分结算"),
    SETTLED("settled", "已结算");

    private final String code;
    private final String label;

    SettlementStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
