package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.bom.BomDTO;
import com.duoduo.jxc.dto.bom.BomItemDTO;
import com.duoduo.jxc.dto.fabric.FabricInventoryDTO;
import com.duoduo.jxc.dto.production.MaterialRequirementDTO;
import com.duoduo.jxc.dto.production.ProductionInboundDTO;
import com.duoduo.jxc.dto.production.ProductionOrderDTO;
import com.duoduo.jxc.dto.production.ProductionOrderQuery;
import com.duoduo.jxc.entity.Fabric;
import com.duoduo.jxc.entity.FabricInventory;
import com.duoduo.jxc.entity.ProductionOrder;
import com.duoduo.jxc.enums.InventoryTransTypeEnum;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.FabricInventoryMapper;
import com.duoduo.jxc.mapper.FabricMapper;
import com.duoduo.jxc.mapper.ProductionOrderMapper;
import com.duoduo.jxc.service.BomService;
import com.duoduo.jxc.service.DocNoRuleService;
import com.duoduo.jxc.service.FabricInventoryService;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder> implements ProductionOrderService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final BomService bomService;
    private final FabricInventoryService fabricInventoryService;
    private final FabricMapper fabricMapper;
    private final FabricInventoryMapper fabricInventoryMapper;
    private final InventoryService inventoryService;
    private final DocNoRuleService docNoRuleService;

    @Override
    public PageResult<ProductionOrderDTO> pageQuery(ProductionOrderQuery query) {
        Page<ProductionOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProductionOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getOrderNo()), ProductionOrder::getOrderNo, query.getOrderNo())
               .like(StringUtils.hasText(query.getStyleNo()), ProductionOrder::getStyleNo, query.getStyleNo())
               .eq(query.getStyleId() != null, ProductionOrder::getStyleId, query.getStyleId())
               .eq(query.getCustomerId() != null, ProductionOrder::getCustomerId, query.getCustomerId())
               .eq(query.getFactoryId() != null, ProductionOrder::getFactoryId, query.getFactoryId())
               .eq(StringUtils.hasText(query.getStatus()), ProductionOrder::getStatus, query.getStatus())
               .eq(StringUtils.hasText(query.getPriority()), ProductionOrder::getPriority, query.getPriority())
               .ge(query.getDeadlineFrom() != null, ProductionOrder::getDeadline, query.getDeadlineFrom())
               .le(query.getDeadlineTo() != null, ProductionOrder::getDeadline, query.getDeadlineTo())
               .and(StringUtils.hasText(query.getKeyword()), w ->
                   w.like(ProductionOrder::getOrderNo, query.getKeyword())
                    .or().like(ProductionOrder::getStyleNo, query.getKeyword())
                    .or().like(ProductionOrder::getStyleName, query.getKeyword()))
               .orderByDesc(ProductionOrder::getCreateTime);
        page(page, wrapper);
        List<ProductionOrderDTO> dtoList = page.getRecords().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProductionOrderDTO getDetail(Long id) {
        ProductionOrder entity = getById(id);
        return entity != null ? toDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProductionOrderDTO dto) {
        ProductionOrder entity = new ProductionOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setOrderNo(generateOrderNo());
        if (entity.getStatus() == null) {
            entity.setStatus("pending");
        }
        if (entity.getCompletedQuantity() == null) {
            entity.setCompletedQuantity(0);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);
        return entity.getOrderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProductionOrderDTO dto) {
        ProductionOrder entity = new ProductionOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        ProductionOrder entity = new ProductionOrder();
        entity.setOrderId(id);
        entity.setStatus(status);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private ProductionOrderDTO toDTO(ProductionOrder entity) {
        ProductionOrderDTO dto = new ProductionOrderDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private String generateOrderNo() {
        return docNoRuleService.generateDocNo("SCRW");
    }

    @Override
    public MaterialRequirementDTO calculateMaterialRequirement(Long productionOrderId) {
        ProductionOrder order = getById(productionOrderId);
        if (order == null) {
            throw new BusinessException(BizCode.PRODUCTION_ORDER_NOT_FOUND, "生产订单不存在");
        }

        MaterialRequirementDTO result = new MaterialRequirementDTO();
        result.setProductionOrderId(productionOrderId);
        result.setOrderNo(order.getOrderNo());
        result.setStyleId(order.getStyleId());
        result.setStyleNo(order.getStyleNo());
        result.setStyleName(order.getStyleName());
        result.setProductionQty(order.getQuantity());

        List<MaterialRequirementDTO.MaterialItemDTO> materials = new ArrayList<>();
        boolean hasShortage = false;

        if (order.getStyleId() != null) {
            List<BomDTO> bomList = bomService.listByStyleId(order.getStyleId());
            if (!bomList.isEmpty()) {
                BomDTO bom = bomList.get(0);
                List<BomItemDTO> bomItems = bomService.getItems(bom.getBomId());

                for (BomItemDTO item : bomItems) {
                    MaterialRequirementDTO.MaterialItemDTO materialItem = new MaterialRequirementDTO.MaterialItemDTO();
                    materialItem.setFabricId(item.getMaterialId());
                    materialItem.setFabricCode(item.getMaterialCode());
                    materialItem.setFabricName(item.getMaterialName());
                    materialItem.setUnit(item.getUnit());
                    materialItem.setUnitUsage(item.getQuantity());
                    materialItem.setWastageRate(item.getWastageRate());

                    BigDecimal wastageRate = item.getWastageRate() != null ? item.getWastageRate() : BigDecimal.ZERO;
                    BigDecimal unitUsage = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
                    BigDecimal requiredQty = unitUsage.multiply(BigDecimal.valueOf(order.getQuantity()))
                            .multiply(BigDecimal.ONE.add(wastageRate))
                            .setScale(2, RoundingMode.HALF_UP);
                    materialItem.setRequiredQty(requiredQty);

                    BigDecimal availableQty = BigDecimal.ZERO;
                    if (item.getMaterialId() != null) {
                        Fabric fabric = fabricMapper.selectById(item.getMaterialId());
                        if (fabric != null) {
                            materialItem.setFabricType(fabric.getFabricType());
                        }

                        LambdaQueryWrapper<FabricInventory> wrapper = new LambdaQueryWrapper<>();
                        wrapper.eq(FabricInventory::getFabricId, item.getMaterialId());
                        List<FabricInventory> inventories = fabricInventoryMapper.selectList(wrapper);
                        for (FabricInventory inv : inventories) {
                            if (inv.getAvailableQty() != null) {
                                availableQty = availableQty.add(inv.getAvailableQty());
                            }
                        }
                    }
                    materialItem.setAvailableQty(availableQty);

                    BigDecimal shortageQty = requiredQty.subtract(availableQty);
                    if (shortageQty.compareTo(BigDecimal.ZERO) < 0) {
                        shortageQty = BigDecimal.ZERO;
                    }
                    materialItem.setShortageQty(shortageQty);

                    boolean sufficient = shortageQty.compareTo(BigDecimal.ZERO) == 0;
                    materialItem.setSufficient(sufficient);
                    if (!sufficient) {
                        hasShortage = true;
                    }

                    materials.add(materialItem);
                }
            }
        }

        result.setMaterials(materials);
        result.setHasShortage(hasShortage);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void productionInbound(ProductionInboundDTO dto) {
        ProductionOrder order = getById(dto.getProductionOrderId());
        if (order == null) {
            throw new BusinessException(BizCode.PRODUCTION_ORDER_NOT_FOUND, "生产订单不存在");
        }

        if (!"producing".equals(order.getStatus()) && !"pending".equals(order.getStatus())) {
            throw new BusinessException(BizCode.OPERATION_NOT_ALLOWED, "当前状态不允许入库");
        }

        int totalInboundQty = 0;
        for (ProductionInboundDTO.InboundItemDTO item : dto.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }

            inventoryService.addStock(
                    dto.getWarehouseId(),
                    item.getSkuId(),
                    item.getQuantity(),
                    InventoryTransTypeEnum.PRODUCTION_IN.name(),
                    dto.getProductionOrderId(),
                    order.getOrderNo()
            );

            totalInboundQty += item.getQuantity();
            log.info("生产入库成功: orderId={}, skuId={}, qty={}", dto.getProductionOrderId(), item.getSkuId(), item.getQuantity());
        }

        int newCompletedQty = (order.getCompletedQuantity() != null ? order.getCompletedQuantity() : 0) + totalInboundQty;
        ProductionOrder updateEntity = new ProductionOrder();
        updateEntity.setOrderId(dto.getProductionOrderId());
        updateEntity.setCompletedQuantity(newCompletedQty);
        updateEntity.setUpdateTime(LocalDateTime.now());

        if (newCompletedQty >= order.getQuantity()) {
            updateEntity.setStatus("completed");
        } else if ("pending".equals(order.getStatus())) {
            updateEntity.setStatus("producing");
        }

        updateById(updateEntity);
        log.info("生产订单入库完成: orderId={}, completedQty={}", dto.getProductionOrderId(), newCompletedQty);
    }
}
