package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.WarehouseDTO;
import com.duoduo.jxc.dto.data.WarehouseQuery;
import com.duoduo.jxc.entity.Warehouse;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.WarehouseMapper;
import com.duoduo.jxc.service.WarehouseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Override
    public PageResult<Warehouse> pageQuery(WarehouseQuery query) {
        Page<Warehouse> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Warehouse::getWarehouseName, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Warehouse::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Warehouse::getWarehouseId);

        Page<Warehouse> resultPage = this.page(page, wrapper);
        return new PageResult<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public Warehouse getDetail(Long id) {
        Warehouse warehouse = this.getById(id);
        if (warehouse == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return warehouse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WarehouseDTO dto) {
        if (!StringUtils.hasText(dto.getWarehouseName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "仓库名称不能为空");
        }

        Warehouse warehouse = new Warehouse();
        BeanUtils.copyProperties(dto, warehouse);
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(1);
        }
        this.save(warehouse);
        return warehouse.getWarehouseId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WarehouseDTO dto) {
        if (dto.getWarehouseId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "仓库ID不能为空");
        }

        Warehouse exist = this.getById(dto.getWarehouseId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Warehouse exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        LambdaUpdateWrapper<Warehouse> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Warehouse::getWarehouseId, id)
                .set(Warehouse::getStatus, status);
        this.update(wrapper);
    }
}
