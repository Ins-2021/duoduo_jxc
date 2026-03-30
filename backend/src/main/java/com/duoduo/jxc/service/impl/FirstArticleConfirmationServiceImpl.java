package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.FirstArticleConfirmationDTO;
import com.duoduo.jxc.dto.FirstArticleConfirmationQuery;
import com.duoduo.jxc.entity.FirstArticleConfirmation;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.FirstArticleConfirmationMapper;
import com.duoduo.jxc.service.FirstArticleConfirmationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FirstArticleConfirmationServiceImpl extends ServiceImpl<FirstArticleConfirmationMapper, FirstArticleConfirmation> implements FirstArticleConfirmationService {

    @Override
    public PageResult<FirstArticleConfirmationDTO> pageQuery(FirstArticleConfirmationQuery query) {
        Page<FirstArticleConfirmation> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FirstArticleConfirmation> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getConfirmationNo()), FirstArticleConfirmation::getConfirmationNo, query.getConfirmationNo());
        wrapper.eq(query.getOrderId() != null, FirstArticleConfirmation::getOrderId, query.getOrderId());
        wrapper.eq(query.getProcessId() != null, FirstArticleConfirmation::getProcessId, query.getProcessId());
        wrapper.like(StringUtils.hasText(query.getResult()), FirstArticleConfirmation::getResult, query.getResult());
        wrapper.eq(query.getCheckerId() != null, FirstArticleConfirmation::getCheckerId, query.getCheckerId());

        page(page, wrapper);

        List<FirstArticleConfirmationDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public FirstArticleConfirmationDTO getDetail(Long id) {
        FirstArticleConfirmation entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(FirstArticleConfirmationDTO dto) {
        FirstArticleConfirmation entity = new FirstArticleConfirmation();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getConfirmationId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FirstArticleConfirmationDTO dto) {
        FirstArticleConfirmation entity = new FirstArticleConfirmation();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitFirstArticle(FirstArticleConfirmationDTO dto) {
        FirstArticleConfirmation confirmation = new FirstArticleConfirmation();
        BeanUtils.copyProperties(dto, confirmation);
        confirmation.setStatus("pending");
        save(confirmation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveFirstArticle(Long id, boolean approved, String comment) {
        FirstArticleConfirmation confirmation = getById(id);
        if (confirmation == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        
        confirmation.setStatus(approved ? "approved" : "rejected");
        confirmation.setApproveComment(comment);
        confirmation.setApproveTime(LocalDateTime.now());
        updateById(confirmation);
    }

    private FirstArticleConfirmationDTO convertToDTO(FirstArticleConfirmation entity) {
        FirstArticleConfirmationDTO dto = new FirstArticleConfirmationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
