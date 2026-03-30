package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.CustomerDTO;
import com.duoduo.jxc.dto.data.CustomerQuery;
import com.duoduo.jxc.entity.Customer;

public interface CustomerService extends IService<Customer> {

    PageResult<Customer> pageQuery(CustomerQuery query);

    Customer getDetail(Long id);

    Long create(CustomerDTO dto);

    void update(CustomerDTO dto);

    void updateStatus(Long id, String status);
}
