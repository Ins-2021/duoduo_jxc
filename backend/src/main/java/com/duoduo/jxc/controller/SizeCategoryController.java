package com.duoduo.jxc.controller;

import com.duoduo.jxc.entity.SizeCategory;
import com.duoduo.jxc.service.SizeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sizecategory")
@RequiredArgsConstructor
public class SizeCategoryController {

    private final SizeCategoryService sizeCategoryService;

}
