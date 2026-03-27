package com.duoduo.jxc.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 列表数据
     */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}
