package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.dto.finance.WriteOffDTO;
import com.duoduo.jxc.entity.WriteOff;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.PageQuery;

/**
 * 核销单Service接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
public interface WriteOffService extends IService<WriteOff> {
    /**
     * 分页查询
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<WriteOffDTO> pageList(PageQuery query);

    /**
     * 根据ID获取详情
     *
     * @param id 主键ID
     * @return 详情DTO
     */
    WriteOffDTO getById(Long id);

    /**
     * 新增
     *
     * @param dto DTO对象
     * @return 主键ID
     */
    Long create(WriteOffDTO dto);

    /**
     * 删除
     *
     * @param id 主键ID
     */
    void delete(Long id);
}
