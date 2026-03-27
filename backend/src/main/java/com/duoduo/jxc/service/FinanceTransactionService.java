package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.finance.FinanceTransactionDTO;
import com.duoduo.jxc.entity.FinanceTransaction;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

/**
 * 财务流水Service接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
public interface FinanceTransactionService extends IService<FinanceTransaction> {
    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<FinanceTransactionDTO> pageList(PageQuery query);

    /**
     * 根据ID获取详情
     *
     * @param id 主键ID
     * @return 详情DTO
     */
    FinanceTransactionDTO getById(Long id);

    /**
     * 创建财务流水记录
     *
     * @param entity 实体对象
     * @return 主键ID
     */
    Long create(FinanceTransaction entity);
}
