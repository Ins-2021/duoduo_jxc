package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.Workshop;
import com.duoduo.jxc.service.WorkshopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workshop")
@RequiredArgsConstructor
public class WorkshopController {

    private final WorkshopService workshopService;

}
