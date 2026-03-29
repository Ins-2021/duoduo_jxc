package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.Workshop;
import com.duoduo.jxc.mapper.WorkshopMapper;
import com.duoduo.jxc.service.WorkshopService;
import org.springframework.stereotype.Service;

@Service
public class WorkshopServiceImpl extends ServiceImpl<WorkshopMapper, Workshop> implements WorkshopService {
}
