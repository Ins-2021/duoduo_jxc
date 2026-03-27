package com.duoduo.jxc.service.rbac.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.rbac.SysPost;
import com.duoduo.jxc.mapper.SysPostMapper;
import com.duoduo.jxc.service.rbac.SysPostService;
import org.springframework.stereotype.Service;

@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {
}