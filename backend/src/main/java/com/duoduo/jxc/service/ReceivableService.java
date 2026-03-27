package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.finance.ReceivableDTO;
import com.duoduo.jxc.entity.Receivable;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

/**
 * 应收账款Service接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
public interface ReceivableService extends IService<Receivable> {
    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<ReceivableDTO> pageList(PageQuery query);

    /**
     * 根据ID获取详情
     *
     * @param id 主键ID
     * @return 详情DTO
     */
    ReceivableDTO getById(Long id);

    /**
     * 新增
     *
     * @param dto DTO对象
     * @return 主键ID
     */
    Long create(ReceivableDTO dto);

    /**
     * 更新
     *
     * @param dto DTO对象
     */
    void update(ReceivableDTO dto);

    /**
     * 删除
     *
     * @param id 主键ID
     */
    void delete(Long id);

    /**
     * 核销应收账款
     *
     * @param id 主键ID
     * @param amount 核销金额
     */
    void writeOff(Long id, java.math.BigDecimal amount);
}
