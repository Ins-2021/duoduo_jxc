package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.InventoryConverter;
import com.duoduo.jxc.dto.inventory.AssemblyOrderDTO;
import com.duoduo.jxc.dto.inventory.AssemblyOrderDetailDTO;
import com.duoduo.jxc.enums.AssemblyOrderStatusEnum;
import com.duoduo.jxc.enums.AssemblyTypeEnum;
import com.duoduo.jxc.entity.AssemblyOrder;
import com.duoduo.jxc.entity.AssemblyOrderDetail;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.AssemblyOrderDetailMapper;
import com.duoduo.jxc.mapper.AssemblyOrderMapper;
import com.duoduo.jxc.service.AssemblyOrderService;
import com.duoduo.jxc.service.InventoryService;
import com.duoduo.jxc.service.DocNoRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssemblyOrderServiceImpl extends ServiceImpl<AssemblyOrderMapper, AssemblyOrder> implements AssemblyOrderService {

    private final AssemblyOrderDetailMapper detailMapper;
    private final InventoryConverter converter;
    private final InventoryService inventoryService;
    private final DocNoRuleService docNoRuleService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public PageResult<AssemblyOrderDTO> pageList(PageQuery query) {
        LambdaQueryWrapper<AssemblyOrder> wrapper = new LambdaQueryWrapper<>();
        String keyword = (String) query.getParams().get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AssemblyOrder::getAssemblyNo, keyword)
                    .or().like(AssemblyOrder::getWarehouseName, keyword);
        }
        Object typeObj = query.getParams().get("type");
        if (typeObj != null && !typeObj.toString().trim().isEmpty()) {
            Integer type = typeObj instanceof Integer ? (Integer) typeObj : Integer.valueOf(typeObj.toString());
            wrapper.eq(AssemblyOrder::getType, type);
        }
        Object statusObj = query.getParams().get("status");
        if (statusObj != null && !statusObj.toString().trim().isEmpty()) {
            Integer status = statusObj instanceof Integer ? (Integer) statusObj : Integer.valueOf(statusObj.toString());
            wrapper.eq(AssemblyOrder::getStatus, status);
        }
        wrapper.orderByDesc(AssemblyOrder::getCreateTime);

        IPage<AssemblyOrder> page = page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<AssemblyOrderDTO> dtoList = page.getRecords().stream()
                .map(converter::toAssemblyOrderDTO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public AssemblyOrderDTO getById(Long id) {
        AssemblyOrder entity = super.getById(id);
        if (entity == null) {
            return null;
        }
        AssemblyOrderDTO dto = converter.toAssemblyOrderDTO(entity);

        List<AssemblyOrderDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<AssemblyOrderDetail>()
                        .eq(AssemblyOrderDetail::getAssemblyId, id)
        );
        dto.setDetails(details.stream()
                .map(converter::toAssemblyOrderDetailDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(AssemblyOrderDTO dto) {
        AssemblyOrder entity = new AssemblyOrder();
        BeanUtils.copyProperties(dto, entity);
        
        entity.setAssemblyNo(generateOrderNo());
        entity.setStatus(AssemblyOrderStatusEnum.DRAFT.getValue());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        save(entity);

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (AssemblyOrderDetailDTO detailDto : dto.getDetails()) {
                AssemblyOrderDetail detail = new AssemblyOrderDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setAssemblyId(entity.getAssemblyId());
                detailMapper.insert(detail);
            }
        }

        return entity.getAssemblyId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AssemblyOrderDTO dto) {
        assertDraft(dto.getAssemblyId(), BizCode.ASSEMBLY_ORDER_CANNOT_MODIFY);
        AssemblyOrder entity = new AssemblyOrder();
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);

        detailMapper.delete(new LambdaQueryWrapper<AssemblyOrderDetail>()
                .eq(AssemblyOrderDetail::getAssemblyId, dto.getAssemblyId()));

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            for (AssemblyOrderDetailDTO detailDto : dto.getDetails()) {
                AssemblyOrderDetail detail = new AssemblyOrderDetail();
                BeanUtils.copyProperties(detailDto, detail);
                detail.setAssemblyId(dto.getAssemblyId());
                detailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        assertDraft(id, BizCode.ASSEMBLY_ORDER_CANNOT_DELETE);
        detailMapper.delete(new LambdaQueryWrapper<AssemblyOrderDetail>()
                .eq(AssemblyOrderDetail::getAssemblyId, id));
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        AssemblyOrder entity = super.getById(id);
        if (entity == null) {
            throw new BusinessException(BizCode.ASSEMBLY_ORDER_NOT_FOUND);
        }
        if (!AssemblyOrderStatusEnum.DRAFT.getValue().equals(entity.getStatus())) {
            throw new BusinessException(BizCode.ASSEMBLY_ORDER_CANNOT_APPROVE);
        }

        List<AssemblyOrderDetail> details = detailMapper.selectList(
                new LambdaQueryWrapper<AssemblyOrderDetail>()
                        .eq(AssemblyOrderDetail::getAssemblyId, id));
        if (details == null || details.isEmpty()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "组装拆卸明细不能为空");
        }

        boolean isAssembly = entity.getType() == null || AssemblyTypeEnum.ASSEMBLY.getValue().equals(entity.getType());
        for (AssemblyOrderDetail detail : details) {
            if (detail.getSkuId() == null || detail.getQty() == null || detail.getQty() <= 0) {
                continue;
            }
            int itemType = detail.getItemType() != null ? detail.getItemType() : 1;
            if (isAssembly) {
                if (itemType == 2) {
                    inventoryService.addStock(entity.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                            "ASSEMBLY", entity.getAssemblyId(), entity.getAssemblyNo());
                } else {
                    inventoryService.deductStock(entity.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                            "ASSEMBLY", entity.getAssemblyId(), entity.getAssemblyNo());
                }
            } else {
                if (itemType == 2) {
                    inventoryService.deductStock(entity.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                            "DISASSEMBLY", entity.getAssemblyId(), entity.getAssemblyNo());
                } else {
                    inventoryService.addStock(entity.getWarehouseId(), detail.getSkuId(), detail.getQty(),
                            "DISASSEMBLY", entity.getAssemblyId(), entity.getAssemblyNo());
                }
            }
        }

        entity.setStatus(AssemblyOrderStatusEnum.APPROVED.getValue());
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
        log.info("组装拆卸审核完成，库存已变动: assemblyId={}, assemblyNo={}, type={}", id, entity.getAssemblyNo(), entity.getType());
    }

    private void assertDraft(Long id, BizCode bizCode) {
        AssemblyOrder exist = super.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.ASSEMBLY_ORDER_NOT_FOUND);
        }
        if (!AssemblyOrderStatusEnum.DRAFT.getValue().equals(exist.getStatus())) {
            throw new BusinessException(bizCode);
        }
    }

    private String generateOrderNo() {
        return docNoRuleService.generateDocNo("ZZ");
    }
}
