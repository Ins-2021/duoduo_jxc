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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 财务流水Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
public class FinanceTransactionServiceImpl extends ServiceImpl<FinanceTransactionMapper, FinanceTransaction> implements FinanceTransactionService {

    private final FinanceConverter converter;

    @Override
    public PageResult<FinanceTransactionDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<FinanceTransaction> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(FinanceTransaction::getTransactionNo, keyword)
                    .or().like(FinanceTransaction::getAccountName, keyword)
                    .or().like(FinanceTransaction::getBillNo, keyword));
        }
        Integer type = (Integer) query.getParams().get("type");
        if (type != null) {
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
