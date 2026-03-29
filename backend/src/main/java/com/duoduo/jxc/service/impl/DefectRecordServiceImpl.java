package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.DefectRecordDTO;
import com.duoduo.jxc.dto.DefectRecordQuery;
import com.duoduo.jxc.entity.DefectRecord;
import com.duoduo.jxc.mapper.DefectRecordMapper;
import com.duoduo.jxc.service.DefectRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefectRecordServiceImpl extends ServiceImpl<DefectRecordMapper, DefectRecord> implements DefectRecordService {

    @Override
    public PageResult<DefectRecordDTO> pageQuery(DefectRecordQuery query) {
        Page<DefectRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<DefectRecord> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getDefectNo()), DefectRecord::getDefectNo, query.getDefectNo());
        wrapper.eq(query.getQualityCheckId() != null, DefectRecord::getQualityCheckId, query.getQualityCheckId());
        wrapper.like(StringUtils.hasText(query.getDefectType()), DefectRecord::getDefectType, query.getDefectType());
        wrapper.like(StringUtils.hasText(query.getHandlingMethod()), DefectRecord::getHandlingMethod, query.getHandlingMethod());
        wrapper.eq(query.getHandledBy() != null, DefectRecord::getHandledBy, query.getHandledBy());

        page(page, wrapper);

        List<DefectRecordDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public DefectRecordDTO getDetail(Long id) {
        DefectRecord entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(DefectRecordDTO dto) {
        DefectRecord entity = new DefectRecord();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getDefectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DefectRecordDTO dto) {
        DefectRecord entity = new DefectRecord();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private DefectRecordDTO convertToDTO(DefectRecord entity) {
        DefectRecordDTO dto = new DefectRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
