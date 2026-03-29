package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.BatchDTO;
import com.duoduo.jxc.dto.inventory.BatchQuery;
import com.duoduo.jxc.entity.Batch;
import com.duoduo.jxc.mapper.BatchMapper;
import com.duoduo.jxc.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchServiceImpl extends ServiceImpl<BatchMapper, Batch> implements BatchService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<BatchDTO> pageQuery(BatchQuery query) {
        Page<Batch> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Batch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getSkuId() != null, Batch::getSkuId, query.getSkuId())
               .eq(query.getWarehouseId() != null, Batch::getWarehouseId, query.getWarehouseId())
               .like(StringUtils.hasText(query.getBatchNo()), Batch::getBatchNo, query.getBatchNo())
               .and(StringUtils.hasText(query.getKeyword()), w -> w.like(Batch::getBatchNo, query.getKeyword()))
               .orderByDesc(Batch::getCreateTime);
        page(page, wrapper);
        List<BatchDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public BatchDTO getDetail(Long id) {
        Batch entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BatchDTO dto) {
        Batch entity = new Batch();
        BeanUtils.copyProperties(dto, entity);
        entity.setBatchNo(generateBatchNo());
        if (entity.getInboundDate() == null) {
            entity.setInboundDate(LocalDate.now());
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getBatchId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BatchDTO dto) {
        Batch entity = new Batch();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private BatchDTO toDTO(Batch entity) {
        BatchDTO dto = new BatchDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateBatchNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "PC" + dateStr + randomStr;
    }
}
