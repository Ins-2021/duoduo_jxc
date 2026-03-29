package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.SizeRatioTemplate;
import com.duoduo.jxc.service.SizeRatioTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sizeratiotemplate")
@RequiredArgsConstructor
public class SizeRatioTemplateController {

    private final SizeRatioTemplateService sizeRatioTemplateService;

}
