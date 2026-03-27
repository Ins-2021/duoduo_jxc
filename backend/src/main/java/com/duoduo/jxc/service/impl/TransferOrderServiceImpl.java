package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.TransferOrderDTO;
import com.duoduo.jxc.dto.inventory.TransferOrderDetailDTO;
import com.duoduo.jxc.entity.TransferOrder;
import com.duoduo.jxc.entity.TransferOrderDetail;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.TransferOrderDetailMapper;
import com.duoduo.jxc.mapper.TransferOrderMapper;
import com.duoduo.jxc.service.TransferOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferOrderServiceImpl extends ServiceImpl<TransferOrderMapper, TransferOrder> implements TransferOrderService {

    private final TransferOrderDetailMapper detailMapper;
    private final InventoryConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<TransferOrderDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<TransferOrder> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(TransferOrder::getTransferNo, keyword)
                    .or().like(TransferOrder::getFromWarehouseName, keyword)
                    .or().like(TransferOrder::getToWarehouseName, keyword));
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(TransferOrder::getStatus, status);
        }
        wrapper.orderByDesc(TransferOrder::getCreateTime);

        IPage<TransferOrder> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<TransferOrderDTO> dtoList = page.getRecords().stream()
                .map(converter::toTransferOrderDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public TransferOrderDTO getById(Long id) {
        TransferOrder entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        TransferOrderDTO dto = converter.toTransferOrderDTO(entity);

        List<TransferOrderDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<TransferOrderDetail>()
                        .eq(TransferOrderDetail::getTransferId, id)
        );
        dto.setDetails(details.stream()
                .map(converter::toTransferOrderDetailDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TransferOrderDTO dto) {
        TransferOrder entity = new TransferOrder();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setTransferNo(generateOrderNo());
        entity.setStatus(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (TransferOrderDetailDTO detailDto : dto.getDetails()) {
                TransferOrderDetail detail = new TransferOrderDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setTransferId(entity.getTransferId());
                detailMapper.insert(detail);
            }
        }

        return entity.getTransferId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TransferOrderDTO dto) {
        TransferOrder entity = new TransferOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);

        detailMapper.delete(new LambdaQueryWrapper<TransferOrderDetail>()
                .eq(TransferOrderDetail::getTransferId, dto.getTransferId()));

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (TransferOrderDetailDTO detailDto : dto.getDetails()) {
                TransferOrderDetail detail = new TransferOrderDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setTransferId(dto.getTransferId());
                detailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        detailMapper.delete(new LambdaQueryWrapper<TransferOrderDetail>()
                .eq(TransferOrderDetail::getTransferId, id));
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        TransferOrder entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_NOT_FOUND);
        }
        if (!entity.getStatus().equals(0)) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_CANNOT_APPROVE);
        }
        entity.setStatus(1);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long id) {
        TransferOrder entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_NOT_FOUND);
        }
        if (!entity.getStatus().equals(0)) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_CANNOT_REJECT);
        }
        entity.setStatus(4);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "DB" + dateStr + randomStr;
    }
}
