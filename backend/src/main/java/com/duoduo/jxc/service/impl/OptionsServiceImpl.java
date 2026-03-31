package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.duoduo.jxc.dto.common.OptionDTO;
import com.duoduo.jxc.entity.Customer;
import com.duoduo.jxc.entity.FinanceAccount;
import com.duoduo.jxc.entity.Store;
import com.duoduo.jxc.entity.Supplier;
import com.duoduo.jxc.entity.SysUser;
import com.duoduo.jxc.entity.Warehouse;
import com.duoduo.jxc.enums.CommonStatusEnum;
import com.duoduo.jxc.mapper.CustomerMapper;
import com.duoduo.jxc.mapper.FinanceAccountMapper;
import com.duoduo.jxc.mapper.StoreMapper;
import com.duoduo.jxc.mapper.SupplierMapper;
import com.duoduo.jxc.mapper.SysUserMapper;
import com.duoduo.jxc.mapper.WarehouseMapper;
import com.duoduo.jxc.service.OptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionsServiceImpl implements OptionsService {
    private final SysUserMapper sysUserMapper;
    private final CustomerMapper customerMapper;
    private final SupplierMapper supplierMapper;
    private final WarehouseMapper warehouseMapper;
    private final FinanceAccountMapper financeAccountMapper;
    private final StoreMapper storeMapper;

    @Override
    public List<OptionDTO> listStores() {
        return storeMapper.selectList(new LambdaQueryWrapper<Store>()
                .eq(Store::getDeleted, 0)
                .eq(Store::getStatus, CommonStatusEnum.ENABLED.getCode())
                .orderByAsc(Store::getStoreName))
            .stream()
            .map(e -> new OptionDTO(e.getStoreId(), e.getStoreName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<OptionDTO> listCustomers() {
        return customerMapper.selectList(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getDeleted, 0)
                .eq(Customer::getStatus, 1)
                .orderByAsc(Customer::getCustomerName))
            .stream()
            .map(e -> new OptionDTO(e.getCustomerId(), e.getCustomerName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<OptionDTO> listSuppliers() {
        return supplierMapper.selectList(new LambdaQueryWrapper<Supplier>()
                .eq(Supplier::getDeleted, 0)
                .eq(Supplier::getStatus, 1)
                .orderByAsc(Supplier::getSupplierName))
            .stream()
            .map(e -> new OptionDTO(e.getSupplierId(), e.getSupplierName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<OptionDTO> listWarehouses() {
        return warehouseMapper.selectList(new LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getDeleted, 0)
                .eq(Warehouse::getStatus, 1)
                .orderByAsc(Warehouse::getWarehouseName))
            .stream()
            .map(e -> new OptionDTO(e.getWarehouseId(), e.getWarehouseName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<OptionDTO> listStaff() {
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, 0)
                .eq(SysUser::getStatus, 1)
                .orderByAsc(SysUser::getCreateTime))
            .stream()
            .map(e -> new OptionDTO(e.getUserId(), StringUtils.hasText(e.getRealName()) ? e.getRealName() : e.getUsername()))
            .collect(Collectors.toList());
    }

    @Override
    public List<OptionDTO> listFinanceAccounts() {
        return financeAccountMapper.selectList(new LambdaQueryWrapper<FinanceAccount>()
                .eq(FinanceAccount::getDeleted, 0)
                .eq(FinanceAccount::getStatus, 1)
                .orderByAsc(FinanceAccount::getAccountName))
            .stream()
            .map(e -> new OptionDTO(e.getAccountId(), e.getAccountName()))
            .collect(Collectors.toList());
    }
}

