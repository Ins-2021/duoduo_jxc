package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.PaymentDTO;
import com.duoduo.jxc.entity.Payment;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.PaymentMapper;
import com.duoduo.jxc.service.PaymentService;
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

/**
 * 付款单Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final FinanceConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<PaymentDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Payment::getPaymentNo, keyword)
                    .or().like(Payment::getSupplierName, keyword));
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(Payment::getStatus, status);
        }
        wrapper.orderByDesc(Payment::getCreateTime);

        IPage<Payment> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<PaymentDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public PaymentDTO getById(Long id) {
        Payment entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PaymentDTO dto) {
        Payment entity = new Payment();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setPaymentNo(generatePaymentNo());
        entity.setStatus(0);
        entity.setCreateTime(LocalDateTime.now());
        save(entity);

        return entity.getPaymentId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PaymentDTO dto) {
        Payment entity = new Payment();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        Payment entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.PAYMENT_ORDER_NOT_FOUND);
        }
        entity.setStatus(1);
        updateById(entity);
    }

    private PaymentDTO toDTO(Payment entity) {
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generatePaymentNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "FK" + dateStr + randomStr;
    }
}
