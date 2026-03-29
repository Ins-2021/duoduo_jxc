package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.SizeOption;
import com.duoduo.jxc.service.SizeOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sizeoption")
@RequiredArgsConstructor
public class SizeOptionController {

    private final SizeOptionService sizeOptionService;

}
