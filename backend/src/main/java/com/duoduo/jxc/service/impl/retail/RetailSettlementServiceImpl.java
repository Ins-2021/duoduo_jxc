package com.duoduo.jxc.service.impl.retail;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.retail.RetailSettlementDTO;
import com.duoduo.jxc.dto.retail.RetailSettlementQuery;
import com.duoduo.jxc.entity.RetailSettlement;
import com.duoduo.jxc.entity.RetailSettlementOrder;
import com.duoduo.jxc.entity.SalesOrder;
import com.duoduo.jxc.enums.RetailSettlementStatusEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.RetailSettlementMapper;
import com.duoduo.jxc.mapper.RetailSettlementOrderMapper;
import com.duoduo.jxc.mapper.SalesOrderMapper;
import com.duoduo.jxc.service.retail.RetailSettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetailSettlementServiceImpl extends ServiceImpl<RetailSettlementMapper, RetailSettlement> implements RetailSettlementService {

    private final RetailSettlementOrderMapper settlementOrderMapper;
    private final SalesOrderMapper salesOrderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDailySettlement(RetailSettlementDTO dto) {
        if (dto.getSettlementDate() == null) {
            throw new BusinessException(BizCode.SETTLEMENT_DATE_EMPTY);
        }

        // 检查当天是否已经日结
        LambdaQueryWrapper<RetailSettlement> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(RetailSettlement::getSettlementDate, dto.getSettlementDate())
                    .eq(RetailSettlement::getStatus, RetailSettlementStatusEnum.NORMAL.getCode());
        if (dto.getStoreId() != null) {
            checkWrapper.eq(RetailSettlement::getStoreId, dto.getStoreId());
        }
        if (count(checkWrapper) > 0) {
            throw new BusinessException(BizCode.SETTLEMENT_ALREADY_EXISTS);
        }

        // 查询当天已核销的零售单
        LambdaQueryWrapper<SalesOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(SalesOrder::getOrderType, 2) // 零售单
                    .eq(SalesOrder::getSettleStatus, 2) // 已核销
                    .eq(SalesOrder::getDocDate, dto.getSettlementDate());
        if (dto.getStoreId() != null) {
            orderWrapper.eq(SalesOrder::getStoreId, dto.getStoreId());
        }

        List<SalesOrder> orders = salesOrderMapper.selectList(orderWrapper);
        if (orders.isEmpty()) {
            throw new BusinessException(BizCode.SETTLEMENT_NO_ORDERS);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SalesOrder order : orders) {
            totalAmount = totalAmount.add(order.getActualAmount() != null ? order.getActualAmount() : BigDecimal.ZERO);
        }

        RetailSettlement settlement = new RetailSettlement();
        String seq = String.valueOf(System.currentTimeMillis()).substring(9);
        settlement.setSettlementNo("RS" + dto.getSettlementDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + seq);
        settlement.setStoreId(dto.getStoreId());
        settlement.setSettlementDate(dto.getSettlementDate());
        settlement.setCashAmount(totalAmount); // 简化处理，全放入现金
        settlement.setWechatAmount(BigDecimal.ZERO);
        settlement.setAlipayAmount(BigDecimal.ZERO);
        settlement.setCardAmount(BigDecimal.ZERO);
        settlement.setOtherAmount(BigDecimal.ZERO);
        settlement.setTotalAmount(totalAmount);
        settlement.setOrderCount(orders.size());
        settlement.setStatus(RetailSettlementStatusEnum.NORMAL.getCode());

        save(settlement);

        // 保存日结单明细
        for (SalesOrder order : orders) {
            RetailSettlementOrder detail = new RetailSettlementOrder();
            detail.setSettlementId(settlement.getSettlementId());
            detail.setSalesOrderId(order.getOrderId());
            detail.setSalesOrderDocNo(order.getDocNo());
            detail.setPaymentMethod("CASH");
            detail.setAmount(order.getActualAmount() != null ? order.getActualAmount() : BigDecimal.ZERO);
            settlementOrderMapper.insert(detail);
        }

        log.info("生成日结单成功: id={}, no={}, amount={}", settlement.getSettlementId(), settlement.getSettlementNo(), totalAmount);
        return settlement.getSettlementId();
    }

    @Override
    public PageResult<RetailSettlementDTO> pageQuery(RetailSettlementQuery query) {
        Page<RetailSettlement> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<RetailSettlement> wrapper = new LambdaQueryWrapper<>();

        if (query.getStoreId() != null) {
            wrapper.eq(RetailSettlement::getStoreId, query.getStoreId());
        }
        if (query.getStartDate() != null) {
            wrapper.ge(RetailSettlement::getSettlementDate, query.getStartDate());
        }
        if (query.getEndDate() != null) {
            wrapper.le(RetailSettlement::getSettlementDate, query.getEndDate());
        }
        if (query.getSettlementNo() != null && !query.getSettlementNo().isEmpty()) {
            wrapper.like(RetailSettlement::getSettlementNo, query.getSettlementNo());
        }
        if (query.getStatus() != null) {
            wrapper.eq(RetailSettlement::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(RetailSettlement::getCreateTime);

        page(page, wrapper);
        List<RetailSettlementDTO> dtoList = page.getRecords().stream().map(entity -> {
            RetailSettlementDTO dto = new RetailSettlementDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }
}