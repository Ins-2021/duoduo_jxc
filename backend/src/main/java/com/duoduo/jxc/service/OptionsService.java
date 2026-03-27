package com.duoduo.jxc.service;

import com.duoduo.jxc.dto.common.OptionDTO;

import java.util.List;

public interface OptionsService {
    List<OptionDTO> listStores();

    List<OptionDTO> listCustomers();

    List<OptionDTO> listSuppliers();

    List<OptionDTO> listWarehouses();

    List<OptionDTO> listStaff();

    List<OptionDTO> listFinanceAccounts();
}

