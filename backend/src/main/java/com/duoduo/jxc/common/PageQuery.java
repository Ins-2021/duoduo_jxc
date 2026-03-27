package com.duoduo.jxc.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 分页查询参数
 *
 * @author duoduo
 */
@Data
public class PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    public void setPageSize(Integer pageSize) {
        if (pageSize != null && pageSize > 500) {
            this.pageSize = 500;
        } else {
            this.pageSize = pageSize;
        }
    }

    /**
     * 扩展查询参数
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 获取扩展参数值
     *
     * @param key 参数键
     * @return 参数值
     */
    public Object getParam(String key) {
        return params.get(key);
    }
}
