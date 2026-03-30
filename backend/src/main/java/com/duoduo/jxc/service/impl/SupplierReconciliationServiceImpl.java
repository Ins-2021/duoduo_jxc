package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.SupplierReconciliationDTO;
import com.duoduo.jxc.dto.data.SupplierReconciliationQuery;
import com.duoduo.jxc.entity.SupplierReconciliation;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SupplierReconciliationMapper;
import com.duoduo.jxc.service.SupplierReconciliationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierReconciliationServiceImpl extends ServiceImpl<SupplierReconciliationMapper, SupplierReconciliation>
        implements SupplierReconciliationService {

    @Override
    public PageResult<SupplierReconciliationDTO> pageQuery(SupplierReconciliationQuery query) {
        Page<SupplierReconciliation> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<SupplierReconciliation> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SupplierReconciliation::getReconNo, query.getKeyword())
                    .or()
                    .like(SupplierReconciliation::getSupplierName, query.getKeyword());
        }
        if (query.getSupplierId() != null) {
            wrapper.eq(SupplierReconciliation::getSupplierId, query.getSupplierId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SupplierReconciliation::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SupplierReconciliation::getReconciliationId);

        Page<SupplierReconciliation> resultPage = this.page(page, wrapper);
        List<SupplierReconciliationDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public SupplierReconciliationDTO getDetail(Long id) {
        SupplierReconciliation entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SupplierReconciliationDTO dto) {
        if (dto.getSupplierId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "供应商ID不能为空");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "对账日期范围不能为空");
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "开始日期不能晚于结束日期");
        }

        SupplierReconciliation entity = new SupplierReconciliation();
        BeanUtils.copyProperties(dto, entity);
        // 初始状态为未对账
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
        // 未付金额 = 应付 - 已付
        BigDecimal unpaid = dto.getTotalAmount() != null && dto.getPaidAmount() != null
                ? dto.getTotalAmount().subtract(dto.getPaidAmount())
                : BigDecimal.ZERO;
        entity.setUnpaidAmount(unpaid);
        this.save(entity);
        return entity.getReconciliationId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SupplierReconciliationDTO dto) {
        if (dto.getReconciliationId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "对账单ID不能为空");
        }

        SupplierReconciliation exist = this.getById(dto.getReconciliationId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        // 只有未对账状态允许修改
        if (exist.getStatus() != null && exist.getStatus() != 0) {
            throw new BusinessException(BizCode.BAD_REQUEST, "非未对账状态不允许修改");
        }

        BeanUtils.copyProperties(dto, exist);
        // 重新计算未付金额
        if (exist.getTotalAmount() != null && exist.getPaidAmount() != null) {
            exist.setUnpaidAmount(exist.getTotalAmount().subtract(exist.getPaidAmount()));
        }
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long id) {
        SupplierReconciliation exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        if (exist.getStatus() != null && exist.getStatus() != 1) {
            throw new BusinessException(BizCode.BAD_REQUEST, "非已对账状态不允许确认");
        }

        LambdaUpdateWrapper<SupplierReconciliation> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SupplierReconciliation::getReconciliationId, id)
                .set(SupplierReconciliation::getStatus, 2);
        this.update(wrapper);
    }

    private SupplierReconciliationDTO convertToDTO(SupplierReconciliation entity) {
        SupplierReconciliationDTO dto = new SupplierReconciliationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
