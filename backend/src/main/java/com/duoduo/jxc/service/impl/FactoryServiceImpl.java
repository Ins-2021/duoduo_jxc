package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.Factory;
import com.duoduo.jxc.mapper.FactoryMapper;
import com.duoduo.jxc.service.FactoryService;
import org.springframework.stereotype.Service;

@Service
public class FactoryServiceImpl extends ServiceImpl<FactoryMapper, Factory> implements FactoryService {
}
