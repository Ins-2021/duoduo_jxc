package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.FinanceTransactionDTO;
import com.duoduo.jxc.entity.FinanceTransaction;
import com.duoduo.jxc.mapper.FinanceTransactionMapper;
import com.duoduo.jxc.service.FinanceTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 财务流水Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceTransactionServiceImpl extends ServiceImpl<FinanceTransactionMapper, FinanceTransaction> implements FinanceTransactionService {

    private final FinanceConverter converter;

    @Override
    public Long create(FinanceTransaction entity) {
        if (entity.getTransactionNo() == null) {
            entity.setTransactionNo(generateTransactionNo());
        }
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(LocalDateTime.now());
        }
        save(entity);
        log.info("创建资金流水: transactionId={}, transactionNo={}", entity.getTransactionId(), entity.getTransactionNo());
        return entity.getTransactionId();
    }

    private String generateTransactionNo() {
        return "JZ" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
    }

    @Override
    public PageResult<FinanceTransactionDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<FinanceTransaction> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(FinanceTransaction::getTransactionNo, keyword)
                    .or().like(FinanceTransaction::getAccountName, keyword)
                    .or().like(FinanceTransaction::getBillNo, keyword));
        }
        Object typeObj = query.getParams().get("type");
        if (typeObj != null && !typeObj.toString().trim().isEmpty()) {
            Integer type = typeObj instanceof Integer ? (Integer) typeObj : Integer.valueOf(typeObj.toString());
            wrapper.eq(FinanceTransaction::getType, type);
        }
        Long accountId = (Long) query.getParams().get("accountId");
        if (accountId != null) {
            wrapper.eq(FinanceTransaction::getAccountId, accountId);
        }
        wrapper.orderByDesc(FinanceTransaction::getCreateTime);

        IPage<FinanceTransaction> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<FinanceTransactionDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public FinanceTransactionDTO getById(Long id) {
        FinanceTransaction entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return toDTO(entity);
    }

    private FinanceTransactionDTO toDTO(FinanceTransaction entity) {
        FinanceTransactionDTO dto = new FinanceTransactionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
