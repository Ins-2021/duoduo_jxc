package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.finance.IncomeExpenseDTO;
import com.duoduo.jxc.entity.IncomeExpense;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

/**
 * 收支单Service接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
public interface IncomeExpenseService extends IService<IncomeExpense> {
    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<IncomeExpenseDTO> pageList(PageQuery query);

    /**
     * 根据ID获取详情
     *
     * @param id 主键ID
     * @return 详情DTO
     */
    IncomeExpenseDTO getById(Long id);

    /**
     * 新增
     *
     * @param dto DTO对象
     * @return 主键ID
     */
    Long create(IncomeExpenseDTO dto);

    /**
     * 更新
     *
     * @param dto DTO对象
     */
    void update(IncomeExpenseDTO dto);

    /**
     * 删除
     *
     * @param id 主键ID
     */
    void delete(Long id);

    /**
     * 审核
     *
     * @param id 主键ID
     */
    void approve(Long id);
}
