package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.SupplierDTO;
import com.duoduo.jxc.dto.data.SupplierQuery;
import com.duoduo.jxc.entity.Supplier;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SupplierMapper;
import com.duoduo.jxc.service.SupplierService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public PageResult<Supplier> pageQuery(SupplierQuery query) {
        Page<Supplier> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword();
            wrapper.and(w -> w.like(Supplier::getSupplierName, keyword)
                    .or()
                    .like(Supplier::getContactName, keyword)
                    .or()
                    .like(Supplier::getContactPhone, keyword));
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Supplier::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Supplier::getSupplierId);

        Page<Supplier> resultPage = this.page(page, wrapper);
        return new PageResult<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public Supplier getDetail(Long id) {
        Supplier supplier = this.getById(id);
        if (supplier == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return supplier;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SupplierDTO dto) {
        if (!StringUtils.hasText(dto.getSupplierName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "供应商名称不能为空");
        }

        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(dto, supplier);
        if (supplier.getStatus() == null) {
            supplier.setStatus(1);
        }
        this.save(supplier);
        return supplier.getSupplierId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SupplierDTO dto) {
        if (dto.getSupplierId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "供应商ID不能为空");
        }

        Supplier exist = this.getById(dto.getSupplierId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        Supplier exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        LambdaUpdateWrapper<Supplier> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Supplier::getSupplierId, id)
                .set(Supplier::getStatus, Integer.parseInt(status));
        this.update(wrapper);
    }
}
