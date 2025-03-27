package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.dto.PriceComparisonResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/price-range")
    public List<PriceComparisonResponse.ByCategory> getPriceRangeByCategory() {
        return categoryService.getPriceRangeByCategory();
    }
} 