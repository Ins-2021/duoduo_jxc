package com.duoduo.jxc.dto.store;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门店查询参数
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreQuery extends PageQuery {

    private String storeCode;

    private String storeName;

    private Integer status;
}
