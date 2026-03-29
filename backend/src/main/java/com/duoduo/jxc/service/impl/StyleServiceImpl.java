package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.StyleDTO;
import com.duoduo.jxc.entity.Style;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.StyleMapper;
import com.duoduo.jxc.service.StyleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StyleServiceImpl extends ServiceImpl<StyleMapper, Style> implements StyleService {

    @Override
    public PageResult<StyleDTO> pageQuery(StyleDTO query) {
        Page<Style> page = new Page<>(1, 10);
        
        LambdaQueryWrapper<Style> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getStyleNo())) {
            wrapper.like(Style::getStyleNo, query.getStyleNo());
        }
        if (StringUtils.hasText(query.getStyleName())) {
            wrapper.like(Style::getStyleName, query.getStyleName());
        }
        wrapper.orderByDesc(Style::getStyleId);

        Page<Style> resultPage = this.page(page, wrapper);
        
        List<StyleDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public StyleDTO getDetail(Long id) {
        Style style = this.getById(id);
        if (style == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(style);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StyleDTO dto) {
        if (!StringUtils.hasText(dto.getStyleNo()) || !StringUtils.hasText(dto.getStyleName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "款式编号和名称不能为空");
        }
        
        checkDuplicate(dto.getStyleNo(), null);
        
        Style style = new Style();
        BeanUtils.copyProperties(dto, style);
        this.save(style);
        return style.getStyleId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StyleDTO dto) {
        if (dto.getStyleId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "款式ID不能为空");
        }
        if (!StringUtils.hasText(dto.getStyleNo()) || !StringUtils.hasText(dto.getStyleName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "款式编号和名称不能为空");
        }

        Style exist = this.getById(dto.getStyleId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getStyleNo(), dto.getStyleId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Style exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(String styleNo, Long excludeId) {
        LambdaQueryWrapper<Style> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Style::getStyleNo, styleNo);
        if (excludeId != null) {
            wrapper.ne(Style::getStyleId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "款式编号已存在");
        }
    }

    private StyleDTO convertToDTO(Style style) {
        StyleDTO dto = new StyleDTO();
        BeanUtils.copyProperties(style, dto);
        return dto;
    }
}
