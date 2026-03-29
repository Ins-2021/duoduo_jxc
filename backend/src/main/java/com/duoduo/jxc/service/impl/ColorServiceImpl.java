package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ColorDTO;
import com.duoduo.jxc.entity.Color;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.ColorMapper;
import com.duoduo.jxc.service.ColorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColorServiceImpl extends ServiceImpl<ColorMapper, Color> implements ColorService {

    @Override
    public PageResult<ColorDTO> pageQuery(PageQuery query) {
        Page<Color> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<Color> wrapper = new LambdaQueryWrapper<>();
        Object keywordObj = query.getParam("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            String keyword = keywordObj.toString();
            wrapper.like(Color::getName, keyword)
                   .or()
                   .like(Color::getCode, keyword);
        }
        wrapper.orderByAsc(Color::getSortOrder).orderByDesc(Color::getColorId);

        Page<Color> resultPage = this.page(page, wrapper);
        
        List<ColorDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public ColorDTO getDetail(Long id) {
        Color color = this.getById(id);
        if (color == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(color);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ColorDTO dto) {
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "颜色编码和名称不能为空");
        }
        
        checkDuplicate(dto.getCode(), null);
        
        Color color = new Color();
        BeanUtils.copyProperties(dto, color);
        this.save(color);
        return color.getColorId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ColorDTO dto) {
        if (dto.getColorId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "颜色ID不能为空");
        }
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "颜色编码和名称不能为空");
        }

        Color exist = this.getById(dto.getColorId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getCode(), dto.getColorId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Color exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(String code, Long excludeId) {
        LambdaQueryWrapper<Color> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Color::getCode, code);
        if (excludeId != null) {
            wrapper.ne(Color::getColorId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "颜色编码已存在");
        }
    }

    private ColorDTO convertToDTO(Color color) {
        ColorDTO dto = new ColorDTO();
        BeanUtils.copyProperties(color, dto);
        return dto;
    }
}
