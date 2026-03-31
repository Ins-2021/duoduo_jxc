package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.store.StoreDTO;
import com.duoduo.jxc.dto.store.StoreQuery;
import com.duoduo.jxc.entity.Store;

import java.util.List;

/**
 * 门店服务接口
 *
 * @author duoduo
 * @since 2026-03-31
 */
public interface StoreService extends IService<Store> {

    /**
     * 分页查询门店
     */
    PageResult<StoreDTO> pageQuery(StoreQuery query);

    /**
     * 获取门店详情
     */
    StoreDTO getDetail(Long storeId);

    /**
     * 新增门店
     */
    Long createStore(StoreDTO dto);

    /**
     * 更新门店
     */
    void updateStore(StoreDTO dto);

    /**
     * 删除门店
     */
    void deleteStore(Long storeId);

    /**
     * 获取所有启用的门店列表
     */
    List<StoreDTO> listEnabled();
}
