package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.Color;
import com.duoduo.jxc.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/color")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

}
