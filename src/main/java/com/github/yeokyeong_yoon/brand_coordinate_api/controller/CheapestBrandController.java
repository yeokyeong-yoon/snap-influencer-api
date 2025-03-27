package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CheapestBrandResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cheapest-brand")
@RequiredArgsConstructor
public class CheapestBrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<CheapestBrandResponse> findCheapestBrandTotal(
            @RequestParam List<Category> categories) {
        return ResponseEntity.ok(brandService.findCheapestBrandTotal(categories));
    }
} 