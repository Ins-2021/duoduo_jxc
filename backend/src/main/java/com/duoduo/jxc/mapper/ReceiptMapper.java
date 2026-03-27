package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.Receipt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收款单Mapper接口
 *
 * @author duoduo
 * @date 2026-03-25
 */
@Mapper
public interface ReceiptMapper extends BaseMapper<Receipt> {
}
