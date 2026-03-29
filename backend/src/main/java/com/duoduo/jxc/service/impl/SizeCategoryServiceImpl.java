package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.SizeCategoryDTO;
import com.duoduo.jxc.entity.SizeCategory;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.SizeCategoryMapper;
import com.duoduo.jxc.service.SizeCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SizeCategoryServiceImpl extends ServiceImpl<SizeCategoryMapper, SizeCategory> implements SizeCategoryService {

    @Override
    public PageResult<SizeCategoryDTO> pageQuery(PageQuery query) {
        Page<SizeCategory> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<SizeCategory> wrapper = new LambdaQueryWrapper<>();
        Object keywordObj = query.getParam("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            String keyword = keywordObj.toString();
            wrapper.like(SizeCategory::getName, keyword)
                   .or()
                   .like(SizeCategory::getCode, keyword);
        }
        wrapper.orderByAsc(SizeCategory::getSortOrder).orderByDesc(SizeCategory::getCategoryId);

        Page<SizeCategory> resultPage = this.page(page, wrapper);
        
        List<SizeCategoryDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public SizeCategoryDTO getDetail(Long id) {
        SizeCategory category = this.getById(id);
        if (category == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SizeCategoryDTO dto) {
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码组编码和名称不能为空");
        }
        
        checkDuplicate(dto.getCode(), null);
        
        SizeCategory category = new SizeCategory();
        BeanUtils.copyProperties(dto, category);
        this.save(category);
        return category.getCategoryId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SizeCategoryDTO dto) {
        if (dto.getCategoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码组ID不能为空");
        }
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "尺码组编码和名称不能为空");
        }

        SizeCategory exist = this.getById(dto.getCategoryId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getCode(), dto.getCategoryId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SizeCategory exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(String code, Long excludeId) {
        LambdaQueryWrapper<SizeCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SizeCategory::getCode, code);
        if (excludeId != null) {
            wrapper.ne(SizeCategory::getCategoryId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "尺码组编码已存在");
        }
    }

    private SizeCategoryDTO convertToDTO(SizeCategory category) {
        SizeCategoryDTO dto = new SizeCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}
