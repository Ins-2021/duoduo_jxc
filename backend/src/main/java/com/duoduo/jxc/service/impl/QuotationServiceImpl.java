package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.sales.QuotationDTO;
import com.duoduo.jxc.dto.sales.QuotationDetailDTO;
import com.duoduo.jxc.dto.sales.QuotationQuery;
import com.duoduo.jxc.dto.sales.SalesOrderDTO;
import com.duoduo.jxc.dto.sales.SalesOrderDetailDTO;
import com.duoduo.jxc.entity.Quotation;
import com.duoduo.jxc.entity.QuotationDetail;
import com.duoduo.jxc.mapper.QuotationDetailMapper;
import com.duoduo.jxc.mapper.QuotationMapper;
import com.duoduo.jxc.service.QuotationService;
import com.duoduo.jxc.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationServiceImpl extends ServiceImpl<QuotationMapper, Quotation> implements QuotationService {

    private final QuotationDetailMapper detailMapper;
    private final SalesOrderService salesOrderService;

    @Override
    public PageResult<QuotationDTO> pageQuery(QuotationQuery query) {
        Page<Quotation> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Quotation> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(query.getCustomerId() != null, Quotation::getCustomerId, query.getCustomerId())
               .eq(StringUtils.hasText(query.getQuotationStatus()), Quotation::getQuotationStatus, query.getQuotationStatus())
               .like(StringUtils.hasText(query.getQuotationNo()), Quotation::getQuotationNo, query.getQuotationNo())
               .like(StringUtils.hasText(query.getCustomerName()), Quotation::getCustomerName, query.getCustomerName())
               .orderByDesc(Quotation::getCreateTime);

        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(Quotation::getQuotationNo, query.getKeyword())
                              .or()
                              .like(Quotation::getCustomerName, query.getKeyword()));
        }

        page(page, wrapper);

        List<QuotationDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public QuotationDTO getDetail(Long id) {
        Quotation quotation = getById(id);
        if (quotation == null) {
            return null;
        }

        QuotationDTO dto = convertToDTO(quotation);
        
        LambdaQueryWrapper<QuotationDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(QuotationDetail::getQuotationId, id).orderByAsc(QuotationDetail::getSortOrder);
        List<QuotationDetail> details = detailMapper.selectList(detailWrapper);
        
        List<QuotationDetailDTO> detailDTOs = details.stream().map(d -> {
            QuotationDetailDTO detailDTO = new QuotationDetailDTO();
            BeanUtils.copyProperties(d, detailDTO);
            return detailDTO;
        }).collect(Collectors.toList());
        
        dto.setDetails(detailDTOs);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(QuotationDTO dto) {
        Quotation quotation = new Quotation();
        BeanUtils.copyProperties(dto, quotation);
        
        // 生成单号
        if (!StringUtils.hasText(quotation.getQuotationNo())) {
            quotation.setQuotationNo("BJ" + LocalDate.now().toString().replace("-", "") + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (quotation.getQuotationStatus() == null) {
            quotation.setQuotationStatus("draft");
        }
        
        save(quotation);
        
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            int sortOrder = 1;
            for (QuotationDetailDTO detailDTO : dto.getDetails()) {
                QuotationDetail detail = new QuotationDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setQuotationId(quotation.getQuotationId());
                detail.setSortOrder(sortOrder++);
                detailMapper.insert(detail);
            }
        }
        
        return quotation.getQuotationId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QuotationDTO dto) {
        Quotation quotation = new Quotation();
        BeanUtils.copyProperties(dto, quotation);
        updateById(quotation);
        
        // 删除旧明细
        LambdaQueryWrapper<QuotationDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(QuotationDetail::getQuotationId, quotation.getQuotationId());
        detailMapper.delete(deleteWrapper);
        
        // 插入新明细
        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            int sortOrder = 1;
            for (QuotationDetailDTO detailDTO : dto.getDetails()) {
                QuotationDetail detail = new QuotationDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setDetailId(null); // 确保是插入
                detail.setQuotationId(quotation.getQuotationId());
                detail.setSortOrder(sortOrder++);
                detailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
        LambdaQueryWrapper<QuotationDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(QuotationDetail::getQuotationId, id);
        detailMapper.delete(deleteWrapper);
    }

    private QuotationDTO convertToDTO(Quotation entity) {
        QuotationDTO dto = new QuotationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long convertToSalesOrder(Long quotationId) {
        Quotation quotation = getById(quotationId);
        if (quotation == null) {
            throw new RuntimeException("报价单不存在");
        }

        // 获取明细
        LambdaQueryWrapper<QuotationDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuotationDetail::getQuotationId, quotationId);
        List<QuotationDetail> details = detailMapper.selectList(wrapper);

        // 构建销售订单DTO
        SalesOrderDTO salesOrderDTO = new SalesOrderDTO();
        salesOrderDTO.setOrderType(1); // 默认批发单
        salesOrderDTO.setCustomerId(quotation.getCustomerId());
        salesOrderDTO.setDocDate(LocalDate.now());
        salesOrderDTO.setTotalAmount(quotation.getTotalAmount());
        salesOrderDTO.setDiscountAmount(quotation.getDiscountAmount());
        salesOrderDTO.setActualAmount(quotation.getFinalAmount());
        salesOrderDTO.setRemark("由报价单 " + quotation.getQuotationNo() + " 转化而来。 " + (quotation.getRemark() != null ? quotation.getRemark() : ""));

        // 构建明细DTO
        List<SalesOrderDetailDTO> detailDTOs = details.stream().map(d -> {
            SalesOrderDetailDTO detailDTO = new SalesOrderDetailDTO();
            detailDTO.setSpuId(d.getStyleId()); // 注意: 根据实际业务逻辑调整ID映射
            detailDTO.setSkuId(d.getSkuId());
            detailDTO.setQty(d.getQuantity());
            detailDTO.setUnitPrice(d.getUnitPrice());
            detailDTO.setLineAmount(d.getAmount());
            detailDTO.setRemark(d.getRemark());
            return detailDTO;
        }).collect(Collectors.toList());

        salesOrderDTO.setDetails(detailDTOs);

        // 创建销售订单
        Long orderId = salesOrderService.createOrder(salesOrderDTO);

        // 更新报价单状态
        Quotation updateQuotation = new Quotation();
        updateQuotation.setQuotationId(quotationId);
        updateQuotation.setQuotationStatus("accepted");
        updateById(updateQuotation);

        return orderId;
    }
}
