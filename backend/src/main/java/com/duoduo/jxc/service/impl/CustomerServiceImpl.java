package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.data.CustomerDTO;
import com.duoduo.jxc.dto.data.CustomerQuery;
import com.duoduo.jxc.entity.Customer;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.CustomerMapper;
import com.duoduo.jxc.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public PageResult<Customer> pageQuery(CustomerQuery query) {
        Page<Customer> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword();
            wrapper.and(w -> w.like(Customer::getCustomerName, keyword)
                    .or()
                    .like(Customer::getContactName, keyword)
                    .or()
                    .like(Customer::getContactPhone, keyword));
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Customer::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Customer::getCustomerId);

        Page<Customer> resultPage = this.page(page, wrapper);
        return new PageResult<>(resultPage.getTotal(), resultPage.getRecords());
    }

    @Override
    public Customer getDetail(Long id) {
        Customer customer = this.getById(id);
        if (customer == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return customer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CustomerDTO dto) {
        if (!StringUtils.hasText(dto.getCustomerName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "客户名称不能为空");
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        // 默认启用状态
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        this.save(customer);
        return customer.getCustomerId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerDTO dto) {
        if (dto.getCustomerId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "客户ID不能为空");
        }

        Customer exist = this.getById(dto.getCustomerId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        Customer exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        LambdaUpdateWrapper<Customer> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Customer::getCustomerId, id)
                .set(Customer::getStatus, Integer.parseInt(status));
        this.update(wrapper);
    }
}
