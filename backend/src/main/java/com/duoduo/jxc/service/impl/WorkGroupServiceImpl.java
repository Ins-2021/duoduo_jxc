package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.BizCode;
import com.duoduo.jxc.common.PageQuery;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.WorkGroupDTO;
import com.duoduo.jxc.entity.WorkGroup;
import com.duoduo.jxc.exception.BusinessException;
import com.duoduo.jxc.mapper.WorkGroupMapper;
import com.duoduo.jxc.service.WorkGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkGroupServiceImpl extends ServiceImpl<WorkGroupMapper, WorkGroup> implements WorkGroupService {

    @Override
    public PageResult<WorkGroupDTO> pageQuery(PageQuery query) {
        Page<WorkGroup> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<WorkGroup> wrapper = new LambdaQueryWrapper<>();
        Object keywordObj = query.getParam("keyword");
        if (keywordObj != null && StringUtils.hasText(keywordObj.toString())) {
            String keyword = keywordObj.toString();
            wrapper.like(WorkGroup::getName, keyword)
                   .or()
                   .like(WorkGroup::getCode, keyword);
        }
        Object factoryIdObj = query.getParam("factoryId");
        if (factoryIdObj != null && StringUtils.hasText(factoryIdObj.toString())) {
            wrapper.eq(WorkGroup::getFactoryId, Long.valueOf(factoryIdObj.toString()));
        }
        Object workshopIdObj = query.getParam("workshopId");
        if (workshopIdObj != null && StringUtils.hasText(workshopIdObj.toString())) {
            wrapper.eq(WorkGroup::getWorkshopId, Long.valueOf(workshopIdObj.toString()));
        }
        wrapper.orderByDesc(WorkGroup::getGroupId);

        Page<WorkGroup> resultPage = this.page(page, wrapper);
        
        List<WorkGroupDTO> dtoList = resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(resultPage.getTotal(), dtoList);
    }

    @Override
    public WorkGroupDTO getDetail(Long id) {
        WorkGroup group = this.getById(id);
        if (group == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        return convertToDTO(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WorkGroupDTO dto) {
        if (dto.getFactoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "所属工厂ID不能为空");
        }
        if (dto.getWorkshopId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "所属车间ID不能为空");
        }
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "班组编码和名称不能为空");
        }
        
        checkDuplicate(dto.getWorkshopId(), dto.getCode(), null);
        
        WorkGroup group = new WorkGroup();
        BeanUtils.copyProperties(dto, group);
        this.save(group);
        return group.getGroupId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WorkGroupDTO dto) {
        if (dto.getGroupId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "班组ID不能为空");
        }
        if (dto.getFactoryId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "所属工厂ID不能为空");
        }
        if (dto.getWorkshopId() == null) {
            throw new BusinessException(BizCode.BAD_REQUEST, "所属车间ID不能为空");
        }
        if (!StringUtils.hasText(dto.getCode()) || !StringUtils.hasText(dto.getName())) {
            throw new BusinessException(BizCode.BAD_REQUEST, "班组编码和名称不能为空");
        }

        WorkGroup exist = this.getById(dto.getGroupId());
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }

        checkDuplicate(dto.getWorkshopId(), dto.getCode(), dto.getGroupId());

        BeanUtils.copyProperties(dto, exist);
        this.updateById(exist);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        WorkGroup exist = this.getById(id);
        if (exist == null) {
            throw new BusinessException(BizCode.NOT_FOUND);
        }
        this.removeById(id);
    }

    private void checkDuplicate(Long workshopId, String code, Long excludeId) {
        LambdaQueryWrapper<WorkGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkGroup::getWorkshopId, workshopId)
               .eq(WorkGroup::getCode, code);
        if (excludeId != null) {
            wrapper.ne(WorkGroup::getGroupId, excludeId);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(BizCode.DUPLICATE, "该车间下班组编码已存在");
        }
    }

    private WorkGroupDTO convertToDTO(WorkGroup group) {
        WorkGroupDTO dto = new WorkGroupDTO();
        BeanUtils.copyProperties(group, dto);
        return dto;
    }
}
