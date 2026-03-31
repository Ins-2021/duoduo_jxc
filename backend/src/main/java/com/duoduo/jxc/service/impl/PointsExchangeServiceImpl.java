package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.PointsExchangeDTO;
import com.duoduo.jxc.entity.PointsExchange;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PointsExchangeMapper;
import com.duoduo.jxc.service.PointsExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointsExchangeServiceImpl extends ServiceImpl<PointsExchangeMapper, PointsExchange> implements PointsExchangeService {

    @Override
    public PageResult<PointsExchangeDTO> pageQuery(int pageNum, int pageSize, String keyword, String startDate, String endDate) {
        Page<PointsExchange> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PointsExchange> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(PointsExchange::getCustomerName, keyword)
                    .or().like(PointsExchange::getProductName, keyword));
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(PointsExchange::getExchangeTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(PointsExchange::getExchangeTime, LocalDate.parse(endDate).plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(PointsExchange::getCreateTime);
        
        page(page, wrapper);
        
        List<PointsExchangeDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PointsExchangeDTO dto) {
        PointsExchange entity = new PointsExchange();
        BeanUtils.copyProperties(dto, entity);
        entity.setStatus(0);
        entity.setExchangeTime(LocalDateTime.now());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);
        save(entity);
        log.info("创建积分兑换记录: customerId={}, productId={}, points={}", 
                entity.getCustomerId(), entity.getProductId(), entity.getPoints());
        return entity.getExchangeId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long exchangeId) {
        PointsExchange entity = getById(exchangeId);
        if (entity == null) {
            throw new BusinessException(BizCode.NOT_FOUND, "兑换记录不存在");
        }
        if (entity.getStatus() != 0) {
            throw new BusinessException(BizCode.BAD_REQUEST, "该兑换记录已确认");
        }
        entity.setStatus(1);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
        log.info("确认发放积分兑换商品: exchangeId={}", exchangeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long exchangeId) {
        PointsExchange entity = getById(exchangeId);
        if (entity == null) {
            return;
        }
        if (entity.getStatus() != 0) {
            throw new BusinessException(BizCode.BAD_REQUEST, "已确认的兑换记录不能删除");
        }
        removeById(exchangeId);
        log.info("删除积分兑换记录: exchangeId={}", exchangeId);
    }

    @Override
    public PointsExchangeDTO getDetail(Long exchangeId) {
        PointsExchange entity = getById(exchangeId);
        return entity != null ? toDTO(entity) : null;
    }

    private PointsExchangeDTO toDTO(PointsExchange entity) {
        PointsExchangeDTO dto = new PointsExchangeDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getExchangeTime() != null) {
            dto.setExchangeTime(entity.getExchangeTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (entity.getCreateTime() != null) {
            dto.setCreateTime(entity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return dto;
    }
}
