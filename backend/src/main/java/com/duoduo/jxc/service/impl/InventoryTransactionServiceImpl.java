package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.inventory.InventoryTransactionDTO;
import com.duoduo.jxc.dto.inventory.InventoryTransactionQuery;
import com.duoduo.jxc.entity.InventoryTransaction;
import com.duoduo.jxc.entity.ProductSku;
import com.duoduo.jxc.entity.ProductSpu;
import com.duoduo.jxc.entity.Warehouse;
import com.duoduo.jxc.enums.InventoryTransTypeEnum;
import com.duoduo.jxc.mapper.InventoryTransactionMapper;
import com.duoduo.jxc.mapper.ProductSkuMapper;
import com.duoduo.jxc.mapper.ProductSpuMapper;
import com.duoduo.jxc.mapper.WarehouseMapper;
import com.duoduo.jxc.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryTransactionServiceImpl
        extends ServiceImpl<InventoryTransactionMapper, InventoryTransaction>
        implements InventoryTransactionService {

    private final WarehouseMapper warehouseMapper;
    private final ProductSkuMapper productSkuMapper;
    private final ProductSpuMapper productSpuMapper;

    @Override
    public void record(Long warehouseId, Long skuId, Integer transType, Integer qty,
                       Integer beforeQty, Integer afterQty,
                       String billType, Long billId, String billNo) {
        InventoryTransaction trans = new InventoryTransaction();
        trans.setWarehouseId(warehouseId);
        trans.setSkuId(skuId);
        trans.setTransType(transType);
        trans.setQty(qty);
        trans.setBeforeQty(beforeQty);
        trans.setAfterQty(afterQty);
        trans.setBillType(billType);
        trans.setBillId(billId);
        trans.setBillNo(billNo);
        save(trans);

        log.debug("库存流水记录: warehouseId={}, skuId={}, transType={}, qty={}",
                warehouseId, skuId, transType, qty);
    }

    @Override
    public PageResult<InventoryTransactionDTO> pageQuery(InventoryTransactionQuery query) {
        Page<InventoryTransaction> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<InventoryTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getWarehouseId() != null, InventoryTransaction::getWarehouseId, query.getWarehouseId())
               .eq(query.getSkuId() != null, InventoryTransaction::getSkuId, query.getSkuId())
               .eq(query.getTransType() != null, InventoryTransaction::getTransType, query.getTransType())
               .like(StringUtils.hasText(query.getBillNo()), InventoryTransaction::getBillNo, query.getBillNo())
               .ge(StringUtils.hasText(query.getStartDate()), InventoryTransaction::getCreateTime, query.getStartDate())
               .le(StringUtils.hasText(query.getEndDate()), InventoryTransaction::getCreateTime, query.getEndDate())
               .orderByDesc(InventoryTransaction::getCreateTime);
        page(page, wrapper);
        List<InventoryTransactionDTO> dtoList = enrichDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public InventoryTransactionDTO getDetail(Long id) {
        InventoryTransaction trans = getById(id);
        if (trans == null) {
            return null;
        }
        List<InventoryTransactionDTO> enriched = enrichDTOList(List.of(trans));
        return enriched.isEmpty() ? null : enriched.get(0);
    }

    private List<InventoryTransactionDTO> enrichDTOList(List<InventoryTransaction> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询关联数据
        List<Long> warehouseIds = list.stream()
                .map(InventoryTransaction::getWarehouseId)
                .filter(w -> w != null)
                .distinct().collect(Collectors.toList());
        Map<Long, Warehouse> warehouseMap = warehouseIds.isEmpty() ? Map.of() :
                warehouseMapper.selectBatchIds(warehouseIds).stream()
                        .collect(Collectors.toMap(Warehouse::getWarehouseId, w -> w));

        List<Long> skuIds = list.stream()
                .map(InventoryTransaction::getSkuId)
                .filter(s -> s != null)
                .distinct().collect(Collectors.toList());
        Map<Long, ProductSku> skuMap = skuIds.isEmpty() ? Map.of() :
                productSkuMapper.selectBatchIds(skuIds).stream()
                        .collect(Collectors.toMap(ProductSku::getSkuId, s -> s));

        List<Long> spuIds = skuMap.values().stream()
                .map(ProductSku::getSpuId)
                .filter(s -> s != null)
                .distinct().collect(Collectors.toList());
        Map<Long, ProductSpu> spuMap = spuIds.isEmpty() ? Map.of() :
                productSpuMapper.selectBatchIds(spuIds).stream()
                        .collect(Collectors.toMap(ProductSpu::getSpuId, s -> s));

        List<InventoryTransactionDTO> dtoList = new ArrayList<>();
        for (InventoryTransaction trans : list) {
            InventoryTransactionDTO dto = new InventoryTransactionDTO();
            dto.setTransactionId(trans.getTransactionId());
            dto.setWarehouseId(trans.getWarehouseId());
            dto.setSkuId(trans.getSkuId());
            dto.setTransType(trans.getTransType());
            dto.setQty(trans.getQty());
            dto.setBeforeQty(trans.getBeforeQty());
            dto.setAfterQty(trans.getAfterQty());
            dto.setBillType(trans.getBillType());
            dto.setBillNo(trans.getBillNo());
            dto.setRemark(trans.getRemark());
            dto.setOperatorId(trans.getOperatorId());
            dto.setCreateTime(trans.getCreateTime());

            // 流水类型名称
            for (InventoryTransTypeEnum e : InventoryTransTypeEnum.values()) {
                if (e.getCode() == trans.getTransType()) {
                    dto.setTransTypeName(e.getLabel());
                    break;
                }
            }

            // 仓库名称
            if (trans.getWarehouseId() != null) {
                Warehouse warehouse = warehouseMap.get(trans.getWarehouseId());
                if (warehouse != null) {
                    dto.setWarehouseName(warehouse.getWarehouseName());
                }
            }

            // SKU信息
            if (trans.getSkuId() != null) {
                ProductSku sku = skuMap.get(trans.getSkuId());
                if (sku != null) {
                    dto.setSkuCode(sku.getSkuCode());
                    if (sku.getSpuId() != null && spuMap.containsKey(sku.getSpuId())) {
                        ProductSpu spu = spuMap.get(sku.getSpuId());
                        dto.setSpuName(spu.getSpuName());
                    }
                }
            }

            dtoList.add(dto);
        }
        return dtoList;
    }
}
