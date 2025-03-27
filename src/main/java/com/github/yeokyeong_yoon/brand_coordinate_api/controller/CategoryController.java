package com.github.yeokyeong_yoon.brand_coordinate_api.controller;

import com.github.yeokyeong_yoon.brand_coordinate_api.domain.Category;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryLowestPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.dto.CategoryPriceResponse;
import com.github.yeokyeong_yoon.brand_coordinate_api.service.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/lowest-prices")
    public CategoryLowestPriceResponse getLowestPricesByCategory() {
        return categoryService.findLowestPricesByCategory();
    }

    @GetMapping("/{category}/price-range")
    public CategoryPriceResponse getPriceRangeByCategory(@PathVariable Category category) {
        return categoryService.findPriceRangeByCategory(category);
    }
} 