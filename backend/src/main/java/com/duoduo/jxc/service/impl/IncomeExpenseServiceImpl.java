package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.IncomeExpenseDTO;
import com.duoduo.jxc.entity.IncomeExpense;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.IncomeExpenseMapper;
import com.duoduo.jxc.service.IncomeExpenseService;
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
 * 收支单Service实现类
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Service
@RequiredArgsConstructor
public class IncomeExpenseServiceImpl extends ServiceImpl<IncomeExpenseMapper, IncomeExpense> implements IncomeExpenseService {

    private final FinanceConverter converter;
    private final com.duoduo.jxc.mapper.FinanceAccountMapper financeAccountMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<IncomeExpenseDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<IncomeExpense> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(IncomeExpense::getIeNo, keyword)
                    .or().like(IncomeExpense::getCategoryName, keyword));
        }
        Object typeObj = query.getParams().get("type");
        if (typeObj != null && !typeObj.toString().trim().isEmpty()) {
            Integer type = typeObj instanceof Integer ? (Integer) typeObj : Integer.valueOf(typeObj.toString());
            wrapper.eq(IncomeExpense::getType, type);
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(IncomeExpense::getStatus, status);
        }
        wrapper.orderByDesc(IncomeExpense::getCreateTime);

        IPage<IncomeExpense> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<IncomeExpenseDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public IncomeExpenseDTO getById(Long id) {
        IncomeExpense entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(IncomeExpenseDTO dto) {
        IncomeExpense entity = new IncomeExpense();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setIeNo(generateIeNo());
        entity.setStatus(0);
        entity.setCreateTime(LocalDateTime.now());
        save(entity);

        return entity.getIeId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IncomeExpenseDTO dto) {
        IncomeExpense entity = new IncomeExpense();
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
    public void approve(Long id) {
        IncomeExpense entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.INCOME_EXPENSE_NOT_FOUND);
        }
        entity.setStatus(1);
        updateById(entity);

        // 更新关联财务账户余额
        if (entity.getAccountId() != null && entity.getAmount() != null) {
            com.duoduo.jxc.entity.FinanceAccount account = financeAccountMapper.selectById(entity.getAccountId());
            if (account != null) {
                java.math.BigDecimal newBalance = account.getBalance() != null ? account.getBalance() : java.math.BigDecimal.ZERO;
                if (entity.getType() == 1) {
                    // 收入：余额增加
                    newBalance = newBalance.add(entity.getAmount());
                } else if (entity.getType() == 2) {
                    // 支出：余额减少
                    newBalance = newBalance.subtract(entity.getAmount());
                }
                account.setBalance(newBalance);
                financeAccountMapper.updateById(account);
            }
        }
    }

    private IncomeExpenseDTO toDTO(IncomeExpense entity) {
        IncomeExpenseDTO dto = new IncomeExpenseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateIeNo() {
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
        String randomStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "SZ" + dateStr + randomStr;
    }
}
