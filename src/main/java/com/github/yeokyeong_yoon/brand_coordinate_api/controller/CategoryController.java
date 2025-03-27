package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/lowest-prices")
    public CategoryLowestPriceResponse getLowestPricesByCategory() {
        return categoryService.findLowestPricesByCategory();
    }

    @GetMapping("/{category}/price-range")
    public CategoryPriceResponse getPriceRangeByCategory(@PathVariable String category) {
        return categoryService.findPriceRangeByCategory(Category.valueOf(category.toUpperCase()));
    }
} 