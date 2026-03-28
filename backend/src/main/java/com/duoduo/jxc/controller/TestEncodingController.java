package com.duoduo.jxc.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/test")
public class TestEncodingController {
    @Value("${spring.datasource.url}")
    private String url;
    @GetMapping("/url")
    public String getUrl() {
        return url;
    }
}
