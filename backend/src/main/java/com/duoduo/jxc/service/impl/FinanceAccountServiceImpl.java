package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FinanceConverter;
import com.duoduo.jxc.dto.finance.FinanceAccountDTO;
import com.duoduo.jxc.dto.finance.FinanceAccountQuery;
import com.duoduo.jxc.entity.FinanceAccount;
import com.duoduo.jxc.mapper.FinanceAccountMapper;
import com.duoduo.jxc.service.FinanceAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceAccountServiceImpl extends ServiceImpl<FinanceAccountMapper, FinanceAccount> implements FinanceAccountService {

    private final FinanceConverter converter;

    @Override
    public PageResult<FinanceAccountDTO> pageQuery(FinanceAccountQuery query) {
        Page<FinanceAccount> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FinanceAccount> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getAccountName()), FinanceAccount::getAccountName, query.getAccountName());

        page(page, wrapper);
        List<FinanceAccountDTO> dtoList = converter.toAccountDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public Long addAccount(FinanceAccountDTO dto) {
        FinanceAccount account = converter.toEntity(dto);
        save(account);
        return account.getAccountId();
    }

    @Override
    public void updateAccount(FinanceAccountDTO dto) {
        FinanceAccount account = converter.toEntity(dto);
        updateById(account);
    }
}
