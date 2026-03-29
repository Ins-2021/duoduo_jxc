package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.Style;
import com.duoduo.jxc.mapper.StyleMapper;
import com.duoduo.jxc.service.StyleService;
import org.springframework.stereotype.Service;

@Service
public class StyleServiceImpl extends ServiceImpl<StyleMapper, Style> implements StyleService {
}
