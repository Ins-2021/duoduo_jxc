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
import com.duoduo.jxc.enums.TransferOrderStatusEnum;
import com.duoduo.jxc.entity.TransferOrder;
import com.duoduo.jxc.entity.TransferOrderDetail;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.TransferOrderDetailMapper;
import com.duoduo.jxc.mapper.TransferOrderMapper;
import com.duoduo.jxc.service.TransferOrderService;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.DocNoRuleService;
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
    private final InventoryService inventoryService;
    private final DocNoRuleService docNoRuleService;

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
        validateWarehouses(dto.getFromWarehouseId(), dto.getToWarehouseId());
        TransferOrder entity = new TransferOrder();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setTransferNo(generateOrderNo());
        entity.setStatus(TransferOrderStatusEnum.PENDING.getValue());
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
        assertPending(dto.getTransferId(), BizCode.TRANSFER_ORDER_CANNOT_MODIFY);
        validateWarehouses(dto.getFromWarehouseId(), dto.getToWarehouseId());
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
        assertPending(id, BizCode.TRANSFER_ORDER_CANNOT_DELETE);
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
        if (!TransferOrderStatusEnum.PENDING.getValue().equals(entity.getStatus())) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_CANNOT_APPROVE);
        }
        validateWarehouses(entity.getFromWarehouseId(), entity.getToWarehouseId());

        List<TransferOrderDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<TransferOrderDetail>()
                        .eq(TransferOrderDetail::getTransferId, id));

        // 调出仓扣减库存，调入仓增加库存
        for (TransferOrderDetail detail : details) {
            if (detail.getSkuId() == null || detail.getQty() == null || detail.getQty() <= 0) {
                continue;
            }
            // 调出仓出库
            inventoryService.deductStock(entity.getFromWarehouseId(), detail.getSkuId(), detail.getQty(),
                    "TRANSFER_OUT", entity.getTransferId(), entity.getTransferNo());
            // 调入仓入库
            inventoryService.addStock(entity.getToWarehouseId(), detail.getSkuId(), detail.getQty(),
                    "TRANSFER_IN", entity.getTransferId(), entity.getTransferNo());
        }

        entity.setStatus(TransferOrderStatusEnum.APPROVED.getValue());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
        log.info("调拨审核完成，库存已双向变动: transferId={}, transferNo={}", id, entity.getTransferNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long id) {
        TransferOrder entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_NOT_FOUND);
        }
        if (!TransferOrderStatusEnum.PENDING.getValue().equals(entity.getStatus())) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_CANNOT_REJECT);
        }
        entity.setStatus(TransferOrderStatusEnum.CANCELLED.getValue());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private void assertPending(Long id, BizCode bizCode) {
        TransferOrder exist = super.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_NOT_FOUND);
        }
        if (!TransferOrderStatusEnum.PENDING.getValue().equals(exist.getStatus())) {
            throw new BusinessException(bizCode);
        }
    }

    private void validateWarehouses(Long fromWarehouseId, Long toWarehouseId) {
        if (fromWarehouseId != null && fromWarehouseId.equals(toWarehouseId)) {
            throw new BusinessException(BizCode.TRANSFER_ORDER_WAREHOUSE_CONFLICT);
        }
    }

    private String generateOrderNo() {
        return docNoRuleService.generateDocNo("DB");
    }
}
