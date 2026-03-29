package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.StyleColorway;
import com.duoduo.jxc.service.StyleColorwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stylecolorway")
@RequiredArgsConstructor
public class StyleColorwayController {

    private final StyleColorwayService styleColorwayService;

}
