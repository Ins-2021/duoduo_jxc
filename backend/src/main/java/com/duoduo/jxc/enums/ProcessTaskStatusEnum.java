package com.duoduo.jxc.enums;

import lombok.Getter;

/**
 * 工序任务状态枚举
 */
@Getter
public enum ProcessTaskStatusEnum implements DictEnum {
    PENDING(0, "待分配"),
    ASSIGNED(1, "已分配"),
    IN_PROGRESS(2, "进行中"),
    COMPLETED(3, "已完成"),
    CANCELLED(90, "已取消");

    private final int code;
    private final String label;

    ProcessTaskStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }
}
