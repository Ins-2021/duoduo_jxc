package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.AssemblyOrderDTO;
import com.duoduo.jxc.dto.inventory.AssemblyOrderDetailDTO;
import com.duoduo.jxc.entity.AssemblyOrder;
import com.duoduo.jxc.entity.AssemblyOrderDetail;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.AssemblyOrderDetailMapper;
import com.duoduo.jxc.mapper.AssemblyOrderMapper;
import com.duoduo.jxc.service.AssemblyOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssemblyOrderServiceImpl extends ServiceImpl<AssemblyOrderMapper, AssemblyOrder> implements AssemblyOrderService {

    private final AssemblyOrderDetailMapper detailMapper;
    private final InventoryConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<AssemblyOrderDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<AssemblyOrder> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AssemblyOrder::getAssemblyNo, keyword)
                    .or().like(AssemblyOrder::getWarehouseName, keyword);
        }
        Object typeObj = query.getParams().get("type");
        if (typeObj != null && !typeObj.toString().trim().isEmpty()) {
            Integer type = typeObj instanceof Integer ? (Integer) typeObj : Integer.valueOf(typeObj.toString());
            wrapper.eq(AssemblyOrder::getType, type);
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(AssemblyOrder::getStatus, status);
        }
        wrapper.orderByDesc(AssemblyOrder::getCreateTime);

        IPage<AssemblyOrder> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<AssemblyOrderDTO> dtoList = page.getRecords().stream()
                .map(converter::toAssemblyOrderDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public AssemblyOrderDTO getById(Long id) {
        AssemblyOrder entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        AssemblyOrderDTO dto = converter.toAssemblyOrderDTO(entity);

        List<AssemblyOrderDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<AssemblyOrderDetail>()
                        .eq(AssemblyOrderDetail::getAssemblyId, id)
        );
        dto.setDetails(details.stream()
                .map(converter::toAssemblyOrderDetailDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(AssemblyOrderDTO dto) {
        AssemblyOrder entity = new AssemblyOrder();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setAssemblyNo(generateOrderNo());
        entity.setStatus(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (AssemblyOrderDetailDTO detailDto : dto.getDetails()) {
                AssemblyOrderDetail detail = new AssemblyOrderDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setAssemblyId(entity.getAssemblyId());
                detailMapper.insert(detail);
            }
        }

        return entity.getAssemblyId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AssemblyOrderDTO dto) {
        AssemblyOrder entity = new AssemblyOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);

        detailMapper.delete(new LambdaQueryWrapper<AssemblyOrderDetail>()
                .eq(AssemblyOrderDetail::getAssemblyId, dto.getAssemblyId()));

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (AssemblyOrderDetailDTO detailDto : dto.getDetails()) {
                AssemblyOrderDetail detail = new AssemblyOrderDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setAssemblyId(dto.getAssemblyId());
                detailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        detailMapper.delete(new LambdaQueryWrapper<AssemblyOrderDetail>()
                .eq(AssemblyOrderDetail::getAssemblyId, id));
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        AssemblyOrder entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.ASSEMBLY_ORDER_NOT_FOUND);
        }
        if (!entity.getStatus().equals(0)) {
            throw new BusinessException(BizCode.ASSEMBLY_ORDER_CANNOT_APPROVE);
        }
        entity.setStatus(1);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ZZ" + dateStr + randomStr;
    }
}
