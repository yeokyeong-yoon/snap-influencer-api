package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/api/test")
    public String test() {
        return "Test endpoint is working!";
    }
} 