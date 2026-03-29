package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.ProcessDTO;
import com.duoduo.jxc.dto.ProcessQuery;
import com.duoduo.jxc.entity.Process;
import com.duoduo.jxc.mapper.ProcessMapper;
import com.duoduo.jxc.service.ProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Override
    public PageResult<ProcessDTO> pageQuery(ProcessQuery query) {
        Page<Process> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.like(StringUtils.hasText(query.getProcessCode()), Process::getProcessCode, query.getProcessCode());
        wrapper.like(StringUtils.hasText(query.getProcessName()), Process::getProcessName, query.getProcessName());
        wrapper.like(StringUtils.hasText(query.getProcessType()), Process::getProcessType, query.getProcessType());

        page(page, wrapper);

        List<ProcessDTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }

    @Override
    public ProcessDTO getDetail(Long id) {
        Process entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ProcessDTO dto) {
        Process entity = new Process();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.getProcessId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProcessDTO dto) {
        Process entity = new Process();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }

    private ProcessDTO convertToDTO(Process entity) {
        ProcessDTO dto = new ProcessDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
