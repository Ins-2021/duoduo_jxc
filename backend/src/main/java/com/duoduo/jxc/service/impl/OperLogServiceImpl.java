package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.oplog.OperLogDTO;
import com.duoduo.jxc.dto.oplog.OperLogQuery;
import com.duoduo.jxc.entity.OperLog;
import com.duoduo.jxc.mapper.OperLogMapper;
import com.duoduo.jxc.service.OperLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {
    @Override
    public PageResult<OperLogDTO> pageQuery(OperLogQuery query) {
        Page<OperLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getContent())) {
            wrapper.like(OperLog::getContent, query.getContent());
        }
        if (query.getOperatorId() != null) {
            wrapper.eq(OperLog::getOperatorId, query.getOperatorId());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge(OperLog::getCreateTime, query.getStartDate() + " 00:00:00");
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le(OperLog::getCreateTime, query.getEndDate() + " 23:59:59");
        }
        wrapper.orderByDesc(OperLog::getCreateTime);

        Page<OperLog> result = page(page, wrapper);

        return new PageResult<>(
            result.getTotal(),
            result.getRecords().stream().map(e -> {
                OperLogDTO dto = new OperLogDTO();
                BeanUtils.copyProperties(e, dto);
                return dto;
            }).collect(Collectors.toList())
        );
    }

    @Override
    public void clearAll() {
        remove(new LambdaQueryWrapper<>());
    }
}

