package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.StyleColorwayDTO;
import com.duoduo.jxc.entity.StyleColorway;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.StyleColorwayMapper;
import com.duoduo.jxc.service.StyleColorwayService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StyleColorwayServiceImpl extends ServiceImpl<StyleColorwayMapper, StyleColorway> implements StyleColorwayService {

    @Override
    public PageResult<StyleColorwayDTO> pageQuery(StyleColorwayDTO query) {
        Page<StyleColorway> page = new Page<>(1, 10);
        
        LambdaQueryWrapper<StyleColorway> wrapper = new LambdaQueryWrapper<>();
        if (query.getStyleId() != null) {
            wrapper.eq(StyleColorway::getStyleId, query.getStyleId());
        }
        wrapper.orderByDesc(StyleColorway::getColorwayId);

        Page<StyleColorway> resultPage = this.page(page, wrapper);
        
        List<StyleColorwayDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public StyleColorwayDTO getDetail(Long id) {
        StyleColorway colorway = this.getById(id);
        if (colorway == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(colorway);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StyleColorwayDTO dto) {
        if (dto.getStyleId() == null || dto.getColorwayNo() == null || dto.getColorwayName() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "款式ID和配色编号、名称不能为空");
        }
        
        checkDuplicate(dto.getStyleId(), dto.getColorwayNo(), null);
        
        StyleColorway colorway = new StyleColorway();
        BeanUtils.copyProperties(dto, colorway);
        this.save(colorway);
        return colorway.getColorwayId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StyleColorwayDTO dto) {
        if (dto.getColorwayId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "配色ID不能为空");
        }
        if (dto.getStyleId() == null || dto.getColorwayNo() == null || dto.getColorwayName() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "款式ID和配色编号、名称不能为空");
        }

        StyleColorway exist = this.getById(dto.getColorwayId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getStyleId(), dto.getColorwayNo(), dto.getColorwayId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StyleColorway exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(Long styleId, String colorwayNo, Long excludeId) {
        LambdaQueryWrapper<StyleColorway> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StyleColorway::getStyleId, styleId)
               .eq(StyleColorway::getColorwayNo, colorwayNo);
        if (excludeId != null) {
            wrapper.ne(StyleColorway::getColorwayId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "该款式下的配色编号已存在");
        }
    }

    private StyleColorwayDTO convertToDTO(StyleColorway colorway) {
        StyleColorwayDTO dto = new StyleColorwayDTO();
        BeanUtils.copyProperties(colorway, dto);
        return dto;
    }
}
