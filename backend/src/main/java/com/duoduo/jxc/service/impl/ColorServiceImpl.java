package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.Color;
import com.duoduo.jxc.mapper.ColorMapper;
import com.duoduo.jxc.service.ColorService;
import org.springframework.stereotype.Service;

@Service
public class ColorServiceImpl extends ServiceImpl<ColorMapper, Color> implements ColorService {
}
