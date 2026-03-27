package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.ReceiptDTO;
import com.duoduo.jxc.entity.Receipt;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.ReceiptMapper;
import com.duoduo.jxc.service.ReceiptService;
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
 * 收款单Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl extends ServiceImpl<ReceiptMapper, Receipt> implements ReceiptService {

    private final FinanceConverter converter;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<ReceiptDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<Receipt> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Receipt::getReceiptNo, keyword)
                    .or().like(Receipt::getCustomerName, keyword));
        }
        Integer status = (Integer) query.getParams().get("status");
        if (status != null) {
            wrapper.eq(Receipt::getStatus, status);
        }
        wrapper.orderByDesc(Receipt::getCreateTime);

        IPage<Receipt> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<ReceiptDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ReceiptDTO getById(Long id) {
        Receipt entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ReceiptDTO dto) {
        Receipt entity = new Receipt();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setReceiptNo(generateReceiptNo());
        entity.setStatus(0);
        entity.setCreateTime(LocalDateTime.now());
        save(entity);

        return entity.getReceiptId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReceiptDTO dto) {
        Receipt entity = new Receipt();
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
        Receipt entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException("收款单不存在");
        }
        entity.setStatus(1);
        updateById(entity);
    }

    private ReceiptDTO toDTO(Receipt entity) {
        ReceiptDTO dto = new ReceiptDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateReceiptNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "SK" + dateStr + randomStr;
    }
}
