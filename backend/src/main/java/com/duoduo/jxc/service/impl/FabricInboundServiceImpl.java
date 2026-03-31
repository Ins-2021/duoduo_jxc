package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FabricConverter;
import com.duoduo.jxc.dto.fabric.FabricInboundDTO;
import com.duoduo.jxc.dto.fabric.FabricInboundQuery;
import com.duoduo.jxc.entity.Fabric;
import com.duoduo.jxc.entity.FabricInbound;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.FabricInboundMapper;
import com.duoduo.jxc.mapper.FabricMapper;
import com.duoduo.jxc.service.FabricInboundService;
import com.duoduo.jxc.service.FabricInventoryService;
import com.duoduo.jxc.service.DocNoRuleService;
import com.duoduo.jxc.enums.FabricInboundStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class FabricInboundServiceImpl extends ServiceImpl<FabricInboundMapper, FabricInbound> implements FabricInboundService {

    private final FabricConverter converter;
    private final FabricMapper fabricMapper;
    private final FabricInventoryService fabricInventoryService;
    private final DocNoRuleService docNoRuleService;
    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public PageResult<FabricInboundDTO> pageQuery(FabricInboundQuery query) {
        Page<FabricInbound> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FabricInbound> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getInboundNo()), FabricInbound::getInboundNo, query.getInboundNo())
               .eq(query.getFabricId() != null, FabricInbound::getFabricId, query.getFabricId())
               .eq(query.getWarehouseId() != null, FabricInbound::getWarehouseId, query.getWarehouseId())
               .eq(query.getSupplierId() != null, FabricInbound::getSupplierId, query.getSupplierId())
               .eq(query.getStatus() != null, FabricInbound::getStatus, query.getStatus())
               .orderByDesc(FabricInbound::getCreateTime);
        page(page, wrapper);
        List<FabricInboundDTO> dtoList = converter.toInboundDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public FabricInboundDTO getDetail(Long id) {
        FabricInbound inbound = getById(id);
        if (inbound == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return converter.toInboundDTO(inbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FabricInboundDTO dto) {
        Fabric fabric = fabricMapper.selectById(dto.getFabricId());
        if (fabric == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        FabricInbound inbound = converter.toEntity(dto);
        inbound.setInboundNo(generateInboundNo());
        inbound.setFabricCode(fabric.getFabricCode());
        inbound.setFabricName(fabric.getFabricName());
        inbound.setStatus(FabricInboundStatusEnum.DRAFT.getCode());
        if (dto.getPrice() != null && dto.getQuantity() != null) {
            inbound.setTotalAmount(dto.getPrice().multiply(dto.getQuantity()));
        }
        if (dto.getInboundDate() == null) {
            inbound.setInboundDate(LocalDateTime.now());
        }
        save(inbound);
        return inbound.getInboundId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        FabricInbound inbound = getById(id);
        if (inbound == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        if (FabricInboundStatusEnum.DRAFT.getCode() != inbound.getStatus()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "只有待审核状态才能审核");
        }
        fabricInventoryService.addStock(
            inbound.getWarehouseId(),
            inbound.getFabricId(),
            inbound.getQuantity(),
            inbound.getBatchNo(),
            inbound.getPrice()
        );
        inbound.setStatus(FabricInboundStatusEnum.APPROVED.getCode());
        updateById(inbound);
    }

    @Override
    public void delete(Long id) {
        FabricInbound inbound = getById(id);
        if (inbound != null && FabricInboundStatusEnum.APPROVED.getCode() == inbound.getStatus()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "已审核的入库单不能删除");
        }
        removeById(id);
    }

    private String generateInboundNo() {
        return docNoRuleService.generateDocNo("MRK");
    }
}
