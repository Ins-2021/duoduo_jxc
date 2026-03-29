package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.SizeChart;
import com.duoduo.jxc.service.SizeChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sizechart")
@RequiredArgsConstructor
public class SizeChartController {

    private final SizeChartService sizeChartService;

}
