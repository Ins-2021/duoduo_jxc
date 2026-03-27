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
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.StockInOutDetailMapper;
import com.duoduo.jxc.mapper.StockInOutMapper;
import com.duoduo.jxc.service.StockInOutService;
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
public class StockInOutServiceImpl extends ServiceImpl<StockInOutMapper, StockInOut> implements StockInOutService {

    private final StockInOutDetailMapper detailMapper;
    private final InventoryConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<StockInOutDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<StockInOut> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.like(StockInOut::getBillNo, keyword)
                    .or().like(StockInOut::getWarehouseName, keyword);
        }
        Integer type = (Integer) query.getParams().get("type");
        if (type != null) {
            wrapper.eq(StockInOut::getType, type);
        }
        Integer status = (Integer) query.getParams().get("status");
        if (status != null) {
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
        entity.setStatus(0);
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
        if (!entity.getStatus().equals(0)) {
            throw new BusinessException(BizCode.STOCK_IN_OUT_CANNOT_APPROVE);
        }
        entity.setStatus(1);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "QT" + dateStr + randomStr;
    }
}
