package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ProcessRecordDTO;
import com.duoduo.jxc.dto.ProcessRecordQuery;
import com.duoduo.jxc.entity.ProcessRecord;
import com.duoduo.jxc.mapper.ProcessRecordMapper;
import com.duoduo.jxc.service.ProcessRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessRecordServiceImpl extends ServiceImpl<ProcessRecordMapper, ProcessRecord> implements ProcessRecordService {

    @Override
    public PageResult<ProcessRecordDTO> pageQuery(ProcessRecordQuery query) {
        Page<ProcessRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(query.getWorkerId() != null, ProcessRecord::getWorkerId, query.getWorkerId());
        wrapper.eq(query.getProcessId() != null, ProcessRecord::getProcessId, query.getProcessId());
        wrapper.eq(query.getBundleId() != null, ProcessRecord::getBundleId, query.getBundleId());

        page(page, wrapper);

        List<ProcessRecordDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProcessRecordDTO getDetail(Long id) {
        ProcessRecord entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProcessRecordDTO dto) {
        ProcessRecord entity = new ProcessRecord();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getRecordId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProcessRecordDTO dto) {
        ProcessRecord entity = new ProcessRecord();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private ProcessRecordDTO convertToDTO(ProcessRecord entity) {
        ProcessRecordDTO dto = new ProcessRecordDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
