package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.StockInOutDTO;
import com.duoduo.jxc.dto.inventory.StockInOutDetailDTO;
import com.duoduo.jxc.entity.StockInOut;
import com.duoduo.jxc.entity.StockInOutDetail;
import com.duoduo.jxc.enums.StockInOutStatusEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.StockInOutDetailMapper;
import com.duoduo.jxc.mapper.StockInOutMapper;
import com.duoduo.jxc.service.StockInOutService;
import com.duoduo.jxc.service.InventoryService;
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
public class StockInOutServiceImpl extends ServiceImpl<StockInOutMapper, StockInOut> implements StockInOutService {

    private final StockInOutDetailMapper detailMapper;
    private final InventoryConverter converter;
    private final InventoryService inventoryService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<StockInOutDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<StockInOut> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.like(StockInOut::getBillNo, keyword)
                    .or().like(StockInOut::getWarehouseName, keyword);
        }
        Object typeObj = query.getParams().get("type");
        if (typeObj != null && !typeObj.toString().trim().isEmpty()) {
            Integer type = typeObj instanceof Integer ? (Integer) typeObj : Integer.valueOf(typeObj.toString());
            wrapper.eq(StockInOut::getType, type);
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(StockInOut::getStatus, status);
        }
        wrapper.orderByDesc(StockInOut::getCreateTime);

        IPage<StockInOut> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<StockInOutDTO> dtoList = page.getRecords().stream()
                .map(converter::toStockInOutDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public StockInOutDTO getById(Long id) {
        StockInOut entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        StockInOutDTO dto = converter.toStockInOutDTO(entity);

        List<StockInOutDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<StockInOutDetail>()
                        .eq(StockInOutDetail::getInOutId, id)
        );
        dto.setDetails(details.stream()
                .map(converter::toStockInOutDetailDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StockInOutDTO dto) {
        StockInOut entity = new StockInOut();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setBillNo(generateOrderNo());
        entity.setStatus(StockInOutStatusEnum.DRAFT.getValue());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (StockInOutDetailDTO detailDto : dto.getDetails()) {
                StockInOutDetail detail = new StockInOutDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setInOutId(entity.getInOutId());
                detailMapper.insert(detail);
            }
        }

        return entity.getInOutId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockInOutDTO dto) {
        assertDraft(dto.getInOutId(), BizCode.STOCK_IN_OUT_CANNOT_MODIFY);
        StockInOut entity = new StockInOut();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);

        detailMapper.delete(new LambdaQueryWrapper<StockInOutDetail>()
                .eq(StockInOutDetail::getInOutId, dto.getInOutId()));

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (StockInOutDetailDTO detailDto : dto.getDetails()) {
                StockInOutDetail detail = new StockInOutDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setInOutId(dto.getInOutId());
                detailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        assertDraft(id, BizCode.STOCK_IN_OUT_CANNOT_DELETE);
        detailMapper.delete(new LambdaQueryWrapper<StockInOutDetail>()
                .eq(StockInOutDetail::getInOutId, id));
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        StockInOut entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.STOCK_IN_OUT_NOT_FOUND);
        }
        if (!StockInOutStatusEnum.DRAFT.getValue().equals(entity.getStatus())) {
            throw new BusinessException(BizCode.STOCK_IN_OUT_CANNOT_APPROVE);
        }
        if (entity.getWarehouseId() == null || entity.getType() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "出入库单仓库和类型不能为空");
        }

        List<StockInOutDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<StockInOutDetail>()
                        .eq(StockInOutDetail::getInOutId, id));
        if (details == null || details.isEmpty()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "出入库明细不能为空");
        }

        boolean isIn = entity.getType() != null && entity.getType() == 1;
        for (StockInOutDetail detail : details) {
            if (detail.getSkuId() == null || detail.getQty() == null || detail.getQty() <= 0) {
                continue;
            }
            if (isIn) {
                inventoryService.addStock(entity.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                        "STOCK_IN_OUT", entity.getInOutId(), entity.getBillNo());
            } else {
                inventoryService.deductStock(entity.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                        "STOCK_IN_OUT", entity.getInOutId(), entity.getBillNo());
            }
        }

        entity.setStatus(StockInOutStatusEnum.APPROVED.getValue());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
        log.info("出入库审核完成，库存已变动: inOutId={}, billNo={}, type={}", id, entity.getBillNo(), entity.getType());
    }

    private void assertDraft(Long id, BizCode bizCode) {
        StockInOut exist = super.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.STOCK_IN_OUT_NOT_FOUND);
        }
        if (!StockInOutStatusEnum.DRAFT.getValue().equals(exist.getStatus())) {
            throw new BusinessException(bizCode);
        }
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "QT" + dateStr + randomStr;
    }
}
