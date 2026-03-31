package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.converter.FabricConverter;
import com.duoduo.jxc.dto.fabric.FabricRequisitionDTO;
import com.duoduo.jxc.dto.fabric.FabricRequisitionQuery;
import com.duoduo.jxc.entity.Fabric;
import com.duoduo.jxc.entity.FabricRequisition;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.FabricMapper;
import com.duoduo.jxc.mapper.FabricRequisitionMapper;
import com.duoduo.jxc.service.FabricInventoryService;
import com.duoduo.jxc.service.FabricRequisitionService;
import com.duoduo.jxc.service.DocNoRuleService;
import com.duoduo.jxc.enums.FabricRequisitionStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class FabricRequisitionServiceImpl extends ServiceImpl<FabricRequisitionMapper, FabricRequisition> implements FabricRequisitionService {

    private final FabricConverter converter;
    private final FabricMapper fabricMapper;
    private final FabricInventoryService fabricInventoryService;
    private final DocNoRuleService docNoRuleService;
    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public PageResult<FabricRequisitionDTO> pageQuery(FabricRequisitionQuery query) {
        Page<FabricRequisition> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FabricRequisition> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRequisitionNo()), FabricRequisition::getRequisitionNo, query.getRequisitionNo())
               .eq(query.getFabricId() != null, FabricRequisition::getFabricId, query.getFabricId())
               .eq(query.getWarehouseId() != null, FabricRequisition::getWarehouseId, query.getWarehouseId())
               .eq(query.getApplicantId() != null, FabricRequisition::getApplicantId, query.getApplicantId())
               .eq(query.getStatus() != null, FabricRequisition::getStatus, query.getStatus())
               .orderByDesc(FabricRequisition::getCreateTime);
        page(page, wrapper);
        List<FabricRequisitionDTO> dtoList = converter.toRequisitionDTOList(page.getRecords());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public FabricRequisitionDTO getDetail(Long id) {
        FabricRequisition requisition = getById(id);
        if (requisition == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return converter.toRequisitionDTO(requisition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FabricRequisitionDTO dto) {
        Fabric fabric = fabricMapper.selectById(dto.getFabricId());
        if (fabric == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        FabricRequisition requisition = converter.toEntity(dto);
        requisition.setRequisitionNo(generateRequisitionNo());
        requisition.setFabricCode(fabric.getFabricCode());
        requisition.setFabricName(fabric.getFabricName());
        requisition.setStatus(FabricRequisitionStatusEnum.DRAFT.getCode());
        save(requisition);
        return requisition.getRequisitionId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, String comment, boolean approved) {
        FabricRequisition requisition = getById(id);
        if (requisition == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        if (FabricRequisitionStatusEnum.DRAFT.getCode() != requisition.getStatus()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "只有待审批状态才能审批");
        }
        requisition.setApproveComment(comment);
        requisition.setApproveTime(LocalDateTime.now());
        if (approved) {
            requisition.setStatus(FabricRequisitionStatusEnum.APPROVED.getCode());
        } else {
            requisition.setStatus(FabricRequisitionStatusEnum.REJECTED.getCode());
        }
        updateById(requisition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issue(Long id) {
        FabricRequisition requisition = getById(id);
        if (requisition == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        if (FabricRequisitionStatusEnum.APPROVED.getCode() != requisition.getStatus()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "只有已审批状态才能发料");
        }
        fabricInventoryService.deductStock(
            requisition.getWarehouseId(),
            requisition.getFabricId(),
            requisition.getQuantity()
        );
        requisition.setStatus(FabricRequisitionStatusEnum.ISSUED.getCode());
        updateById(requisition);
    }

    @Override
    public void delete(Long id) {
        FabricRequisition requisition = getById(id);
        if (requisition != null && FabricRequisitionStatusEnum.DRAFT.getCode() != requisition.getStatus()) {
            throw new BusinessException(BizCode.BAD_REQUEST, "只有待审批状态才能删除");
        }
        removeById(id);
    }

    private String generateRequisitionNo() {
        return docNoRuleService.generateDocNo("MLL");
    }
}
