package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.finance.PaymentDTO;
import com.duoduo.jxc.entity.Payment;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

/**
 * 付款单Service接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
public interface PaymentService extends IService<Payment> {
    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<PaymentDTO> pageList(PageQuery query);

    /**
     * 根据ID获取详情
     *
     * @param id 主键ID
     * @return 详情DTO
     */
    PaymentDTO getById(Long id);

    /**
     * 新增
     *
     * @param dto DTO对象
     * @return 主键ID
     */
    Long create(PaymentDTO dto);

    /**
     * 更新
     *
     * @param dto DTO对象
     */
    void update(PaymentDTO dto);

    /**
     * 删除
     *
     * @param id 主键ID
     */
    void delete(Long id);

    /**
     * 完成付款
     *
     * @param id 主键ID
     */
    void complete(Long id);
}
