package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.store.StoreDTO;
import com.duoduo.jxc.dto.store.StoreQuery;
import com.duoduo.jxc.entity.Store;
import com.duoduo.jxc.enums.CommonStatusEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.StoreMapper;
import com.duoduo.jxc.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 门店服务实现类
 *
 * @author duoduo
 * @since 2026-03-31
 */
@Service
@RequiredArgsConstructor
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    @Override
    public PageResult<StoreDTO> pageQuery(StoreQuery query) {
        Page<Store> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getStoreCode()), Store::getStoreCode, query.getStoreCode())
                .like(StringUtils.hasText(query.getStoreName()), Store::getStoreName, query.getStoreName())
                .eq(query.getStatus() != null, Store::getStatus, query.getStatus())
                .orderByDesc(Store::getCreateTime);

        page(page, wrapper);
        
        List<StoreDTO> list = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(page.getTotal(), list);
    }

    @Override
    public StoreDTO getDetail(Long storeId) {
        Store store = getById(storeId);
        if (store == null) {
            return null;
        }
        return toDTO(store);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStore(StoreDTO dto) {
        validateStoreCode(dto.getStoreCode(), null);
        Store store = toEntity(dto);
        save(store);
        return store.getStoreId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStore(StoreDTO dto) {
        Store exist = getById(dto.getStoreId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        validateStoreCode(dto.getStoreCode(), dto.getStoreId());
        Store store = toEntity(dto);
        updateById(store);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStore(Long storeId) {
        removeById(storeId);
    }

    @Override
    public List<StoreDTO> listEnabled() {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, CommonStatusEnum.ENABLED.getCode())
                .orderByAsc(Store::getStoreName);
        
        return list(wrapper).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private void validateStoreCode(String storeCode, Long excludeId) {
        if (!StringUtils.hasText(storeCode)) {
            return;
        }
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStoreCode, storeCode);
        if (excludeId != null) {
            wrapper.ne(Store::getStoreId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BusinessException("门店编码已存在");
        }
    }

    private StoreDTO toDTO(Store entity) {
        StoreDTO dto = new StoreDTO();
        dto.setStoreId(entity.getStoreId());
        dto.setStoreCode(entity.getStoreCode());
        dto.setStoreName(entity.getStoreName());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        dto.setContactName(entity.getContactName());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        return dto;
    }

    private Store toEntity(StoreDTO dto) {
        Store entity = new Store();
        entity.setStoreId(dto.getStoreId());
        entity.setStoreCode(dto.getStoreCode());
        entity.setStoreName(dto.getStoreName());
        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
        entity.setContactName(dto.getContactName());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
        return entity;
    }
}
