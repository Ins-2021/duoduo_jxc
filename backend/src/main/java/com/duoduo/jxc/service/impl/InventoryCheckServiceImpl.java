package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.InventoryCheckDTO;
import com.duoduo.jxc.dto.inventory.InventoryCheckDetailDTO;
import com.duoduo.jxc.entity.InventoryCheck;
import com.duoduo.jxc.entity.InventoryCheckDetail;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.InventoryCheckDetailMapper;
import com.duoduo.jxc.mapper.InventoryCheckMapper;
import com.duoduo.jxc.service.InventoryCheckService;
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
public class InventoryCheckServiceImpl extends ServiceImpl<InventoryCheckMapper, InventoryCheck> implements InventoryCheckService {

    private final InventoryCheckDetailMapper detailMapper;
    private final InventoryConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<InventoryCheckDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<InventoryCheck> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(InventoryCheck::getCheckNo, keyword)
                    .or().like(InventoryCheck::getWarehouseName, keyword));
        }
        Integer status = (Integer) query.getParams().get("status");
        if (status != null) {
            wrapper.eq(InventoryCheck::getStatus, status);
        }
        wrapper.orderByDesc(InventoryCheck::getCreateTime);

        IPage<InventoryCheck> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<InventoryCheckDTO> dtoList = page.getRecords().stream()
                .map(converter::toInventoryCheckDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public InventoryCheckDTO getById(Long id) {
        InventoryCheck entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        InventoryCheckDTO dto = converter.toInventoryCheckDTO(entity);

        List<InventoryCheckDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<InventoryCheckDetail>()
                        .eq(InventoryCheckDetail::getCheckId, id)
        );
        dto.setDetails(details.stream()
                .map(converter::toInventoryCheckDetailDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(InventoryCheckDTO dto) {
        InventoryCheck entity = new InventoryCheck();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setCheckNo(generateOrderNo());
        entity.setStatus(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (InventoryCheckDetailDTO detailDto : dto.getDetails()) {
                InventoryCheckDetail detail = new InventoryCheckDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setCheckId(entity.getCheckId());
                detailMapper.insert(detail);
            }
        }

        return entity.getCheckId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InventoryCheckDTO dto) {
        InventoryCheck entity = new InventoryCheck();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);

        detailMapper.delete(new LambdaQueryWrapper<InventoryCheckDetail>()
                .eq(InventoryCheckDetail::getCheckId, dto.getCheckId()));

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (InventoryCheckDetailDTO detailDto : dto.getDetails()) {
                InventoryCheckDetail detail = new InventoryCheckDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setCheckId(dto.getCheckId());
                detailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        detailMapper.delete(new LambdaQueryWrapper<InventoryCheckDetail>()
                .eq(InventoryCheckDetail::getCheckId, id));
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        InventoryCheck entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.INVENTORY_CHECK_NOT_FOUND);
        }
        entity.setStatus(2);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PD" + dateStr + randomStr;
    }
}
