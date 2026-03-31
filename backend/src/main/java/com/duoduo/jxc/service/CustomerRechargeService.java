package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.CustomerRechargeDTO;
import com.duoduo.jxc.entity.CustomerRecharge;

public interface CustomerRechargeService extends IService<CustomerRecharge> {

    PageResult<CustomerRechargeDTO> pageQuery(int pageNum, int pageSize, String keyword, String startDate, String endDate);

    Long recharge(CustomerRechargeDTO dto);

    CustomerRechargeDTO getDetail(Long rechargeId);
}
