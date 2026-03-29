package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.Factory;
import com.duoduo.jxc.service.FactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factory")
@RequiredArgsConstructor
public class FactoryController {

    private final FactoryService factoryService;

}
