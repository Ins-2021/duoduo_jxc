package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.home.HomeOverviewDTO;
import com.duoduo.jxc.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/overview")
    public Result<HomeOverviewDTO> overview() {
        return Result.success(homeService.getOverview());
    }
}

