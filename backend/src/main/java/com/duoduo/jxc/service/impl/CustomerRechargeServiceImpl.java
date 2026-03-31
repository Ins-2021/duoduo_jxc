package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.CustomerRechargeDTO;
import com.duoduo.jxc.entity.Customer;
import com.duoduo.jxc.entity.CustomerRecharge;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.CustomerMapper;
import com.duoduo.jxc.mapper.CustomerRechargeMapper;
import com.duoduo.jxc.service.CustomerRechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerRechargeServiceImpl extends ServiceImpl<CustomerRechargeMapper, CustomerRecharge> implements CustomerRechargeService {

    private final CustomerMapper customerMapper;

    @Override
    public PageResult<CustomerRechargeDTO> pageQuery(int pageNum, int pageSize, String keyword, String startDate, String endDate) {
        Page<CustomerRecharge> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CustomerRecharge> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CustomerRecharge::getCustomerName, keyword);
        }
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(CustomerRecharge::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (StringUtils.hasText(endDate)) {
            wrapper.le(CustomerRecharge::getCreateTime, LocalDate.parse(endDate).plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(CustomerRecharge::getCreateTime);
        
        page(page, wrapper);
        
        List<CustomerRechargeDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long recharge(CustomerRechargeDTO dto) {
        Customer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null) {
            throw new BusinessException(BizCode.NOT_FOUND, "客户不存在");
        }
        
        BigDecimal balanceBefore = BigDecimal.ZERO;
        
        CustomerRecharge entity = new CustomerRecharge();
        BeanUtils.copyProperties(dto, entity);
        entity.setCustomerName(customer.getCustomerName());
        entity.setBalanceBefore(balanceBefore);
        entity.setBalanceAfter(balanceBefore.add(dto.getAmount()).add(dto.getGiftAmount() != null ? dto.getGiftAmount() : BigDecimal.ZERO));
        entity.setCreateTime(LocalDateTime.now());
        entity.setDeleted(0);
        save(entity);
        
        log.info("客户充值: customerId={}, amount={}, giftAmount={}", 
                entity.getCustomerId(), entity.getAmount(), entity.getGiftAmount());
        return entity.getRechargeId();
    }

    @Override
    public CustomerRechargeDTO getDetail(Long rechargeId) {
        CustomerRecharge entity = getById(rechargeId);
        return entity != null ? toDTO(entity) : null;
    }

    private CustomerRechargeDTO toDTO(CustomerRecharge entity) {
        CustomerRechargeDTO dto = new CustomerRechargeDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getCreateTime() != null) {
            dto.setCreateTime(entity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return dto;
    }
}
