package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.WorkGroup;
import com.duoduo.jxc.mapper.WorkGroupMapper;
import com.duoduo.jxc.service.WorkGroupService;
import org.springframework.stereotype.Service;

@Service
public class WorkGroupServiceImpl extends ServiceImpl<WorkGroupMapper, WorkGroup> implements WorkGroupService {
}
