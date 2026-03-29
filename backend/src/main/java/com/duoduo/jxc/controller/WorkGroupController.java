package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.WorkGroup;
import com.duoduo.jxc.service.WorkGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workgroup")
@RequiredArgsConstructor
public class WorkGroupController {

    private final WorkGroupService workGroupService;

}
