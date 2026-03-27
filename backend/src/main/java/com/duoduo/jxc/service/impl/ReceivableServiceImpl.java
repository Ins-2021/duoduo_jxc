package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.ReceivableDTO;
import com.duoduo.jxc.entity.Receivable;
import com.duoduo.jxc.mapper.ReceivableMapper;
import com.duoduo.jxc.service.ReceivableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 应收账款Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
public class ReceivableServiceImpl extends ServiceImpl<ReceivableMapper, Receivable> implements ReceivableService {

    private final FinanceConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<ReceivableDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<Receivable> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Receivable::getBillNo, keyword)
                    .or().like(Receivable::getCustomerName, keyword));
        }
        Integer status = (Integer) query.getParams().get("status");
        if (status != null) {
            wrapper.eq(Receivable::getStatus, status);
        }
        wrapper.orderByDesc(Receivable::getCreateTime);

        IPage<Receivable> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<ReceivableDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ReceivableDTO getById(Long id) {
        Receivable entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ReceivableDTO dto) {
        Receivable entity = new Receivable();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setBillNo(generateBillNo());
        entity.setStatus(0);
        entity.setReceivedAmount(BigDecimal.ZERO);
        entity.setRemainingAmount(entity.getAmount());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        return entity.getReceivableId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReceivableDTO dto) {
        Receivable entity = new Receivable();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeOff(Long id, BigDecimal amount) {
        Receivable entity = super.getById(id);
        if (entity == null) {
            throw new com.duoduo.jxc.exception.BusinessException("应收账款不存在");
        }
        if (entity.getRemainingAmount().compareTo(amount) < 0) {
            throw new com.duoduo.jxc.exception.BusinessException("核销金额不能大于剩余金额");
        }
        
        entity.setReceivedAmount(entity.getReceivedAmount().add(amount));
        entity.setRemainingAmount(entity.getRemainingAmount().subtract(amount));
        
        if (entity.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            entity.setStatus(2); // 已核销
        } else {
            entity.setStatus(1); // 部分核销
        }
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private ReceivableDTO toDTO(Receivable entity) {
        ReceivableDTO dto = new ReceivableDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateBillNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "YS" + dateStr + randomStr;
    }
}
