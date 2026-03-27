package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.finance.FinanceAccountDTO;
import com.duoduo.jxc.dto.finance.FinanceAccountQuery;
import com.duoduo.jxc.entity.FinanceAccount;

public interface FinanceAccountService extends IService<FinanceAccount> {
    PageResult<FinanceAccountDTO> pageQuery(FinanceAccountQuery query);
    Long addAccount(FinanceAccountDTO dto);
}
