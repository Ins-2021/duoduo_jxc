package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeRatioTemplateDTO;
import com.duoduo.jxc.entity.SizeRatioTemplate;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SizeRatioTemplateMapper;
import com.duoduo.jxc.service.SizeRatioTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeRatioTemplateServiceImpl extends ServiceImpl<SizeRatioTemplateMapper, SizeRatioTemplate> implements SizeRatioTemplateService {

    @Override
    public PageResult<SizeRatioTemplateDTO> pageQuery(PageQuery query) {
        Page<SizeRatioTemplate> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<SizeRatioTemplate> wrapper = new LambdaQueryWrapper<>();
        Object keywordObj = query.getParam("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            wrapper.like(SizeRatioTemplate::getName, keywordObj.toString());
        }
        Object categoryIdObj = query.getParam("categoryId");
        if (categoryIdObj != null && StringUtils.hasText(categoryIdObj.toString())) {
            wrapper.eq(SizeRatioTemplate::getCategoryId, Long.valueOf(categoryIdObj.toString()));
        }
        wrapper.orderByDesc(SizeRatioTemplate::getTemplateId);

        Page<SizeRatioTemplate> resultPage = this.page(page, wrapper);
        
        List<SizeRatioTemplateDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public SizeRatioTemplateDTO getDetail(Long id) {
        SizeRatioTemplate template = this.getById(id);
        if (template == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SizeRatioTemplateDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "模板名称不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码组ID不能为空");
        }
        
        checkDuplicate(dto.getName(), null);
        
        SizeRatioTemplate template = new SizeRatioTemplate();
        BeanUtils.copyProperties(dto, template);
        this.save(template);
        return template.getTemplateId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SizeRatioTemplateDTO dto) {
        if (dto.getTemplateId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "模板ID不能为空");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "模板名称不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码组ID不能为空");
        }

        SizeRatioTemplate exist = this.getById(dto.getTemplateId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getName(), dto.getTemplateId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SizeRatioTemplate exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(String name, Long excludeId) {
        LambdaQueryWrapper<SizeRatioTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SizeRatioTemplate::getName, name);
        if (excludeId != null) {
            wrapper.ne(SizeRatioTemplate::getTemplateId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "模板名称已存在");
        }
    }

    private SizeRatioTemplateDTO convertToDTO(SizeRatioTemplate template) {
        SizeRatioTemplateDTO dto = new SizeRatioTemplateDTO();
        BeanUtils.copyProperties(template, dto);
        return dto;
    }
}
