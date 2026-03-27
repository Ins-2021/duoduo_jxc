package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.InventoryDTO;
import com.duoduo.jxc.dto.inventory.InventoryQuery;
import com.duoduo.jxc.entity.Inventory;
import com.duoduo.jxc.mapper.InventoryMapper;
import com.duoduo.jxc.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private final InventoryConverter converter;

    @Override
    public PageResult<InventoryDTO> pageQuery(InventoryQuery query) {
        Page<Inventory> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(query.getWarehouseId() != null, Inventory::getWarehouseId, query.getWarehouseId())
               .eq(query.getSkuId() != null, Inventory::getSkuId, query.getSkuId());

        page(page, wrapper);
        List<InventoryDTO> dtoList = converter.toDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }
}
