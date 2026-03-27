package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.FinanceTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 财务流水Mapper接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Mapper
public interface FinanceTransactionMapper extends BaseMapper<FinanceTransaction> {
}
