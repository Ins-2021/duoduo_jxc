package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 付款单Mapper接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
