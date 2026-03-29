package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.WorkshopDTO;
import com.duoduo.jxc.entity.Workshop;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.WorkshopMapper;
import com.duoduo.jxc.service.WorkshopService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkshopServiceImpl extends ServiceImpl<WorkshopMapper, Workshop> implements WorkshopService {

    @Override
    public PageResult<WorkshopDTO> pageQuery(PageQuery query) {
        Page<Workshop> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Workshop> wrapper = new LambdaQueryWrapper<>();
        Object keywordObj = query.getParam("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            String keyword = keywordObj.toString();
            wrapper.like(Workshop::getName, keyword)
                   .or()
                   .like(Workshop::getCode, keyword);
        }
        Object factoryIdObj = query.getParam("factoryId");
        if (factoryIdObj != null) {
            wrapper.eq(Workshop::getFactoryId, Long.valueOf(factoryIdObj.toString()));
        }
        wrapper.orderByDesc(Workshop::getWorkshopId);

        Page<Workshop> resultPage = this.page(page, wrapper);

        List<WorkshopDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public WorkshopDTO getDetail(Long id) {
        Workshop workshop = this.getById(id);
        if (workshop == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(workshop);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WorkshopDTO dto) {
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "车间编码和名称不能为空");
        }

        checkDuplicate(dto.getCode(), dto.getFactoryId(), null);

        Workshop workshop = new Workshop();
        BeanUtils.copyProperties(dto, workshop);
        this.save(workshop);
        return workshop.getWorkshopId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WorkshopDTO dto) {
        if (dto.getWorkshopId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "车间ID不能为空");
        }
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "车间编码和名称不能为空");
        }

        Workshop exist = this.getById(dto.getWorkshopId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getCode(), dto.getFactoryId(), dto.getWorkshopId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Workshop exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(String code, Long factoryId, Long excludeId) {
        LambdaQueryWrapper<Workshop> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Workshop::getCode, code);
        if (factoryId != null) {
            wrapper.eq(Workshop::getFactoryId, factoryId);
        }
        if (excludeId != null) {
            wrapper.ne(Workshop::getWorkshopId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "该工厂下车间编码已存在");
        }
    }

    private WorkshopDTO convertToDTO(Workshop workshop) {
        WorkshopDTO dto = new WorkshopDTO();
        BeanUtils.copyProperties(workshop, dto);
        return dto;
    }
}
