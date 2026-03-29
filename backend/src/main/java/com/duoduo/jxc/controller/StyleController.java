package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.Style;
import com.duoduo.jxc.service.StyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/style")
@RequiredArgsConstructor
public class StyleController {

    private final StyleService styleService;

}
